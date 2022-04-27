import javax.bluetooth.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PoCDiscoverer {

    private static final Map<String, RemoteDevice> remoteDeviceMap = new ConcurrentHashMap<>();

    private static final Lock mutex = new ReentrantLock();
    private static final Condition conditionInquiryCompleted = mutex.newCondition();


    public static Map<String, RemoteDevice> searchDevicesSynchronous(DiscoveryAgent discoverAgent) throws BluetoothStateException, InterruptedException {
        remoteDeviceMap.clear();

        mutex.lock();

        var listener = new PoCListener();

        try {
            if (!discoverAgent.startInquiry(DiscoveryAgent.GIAC, listener)) {
                System.err.println("Couldn't start GIAC inquiry");
                System.exit(2);
            }

            while (!listener.isInquiryCompleted()) {
                conditionInquiryCompleted.await();
            }

            int[] attrIDs = new int[]{
                    0x0100 // Service name
            };

        } finally {
            mutex.unlock();
        }

        return Collections.unmodifiableMap(remoteDeviceMap);
    }

    public static void searchServicesOnDeviceSynchronous(DiscoveryAgent agent, RemoteDevice device) throws BluetoothStateException, InterruptedException {

        mutex.lock();

        try {
            var listener = new PoCListener();
            agent.searchServices(null, new UUID[]{new UUID(0x1106 /*OBEX_FILE_TRANSFER*/)}, device, listener);

            while (!listener.isInquiryCompleted()) {
                conditionInquiryCompleted.await();
            }

        } finally {
            mutex.unlock();
        }

    }


    private static class PoCListener implements DiscoveryListener {


        private final AtomicBoolean inquiryCompleted = new AtomicBoolean(false);


        public boolean isInquiryCompleted() {
            return inquiryCompleted.get();
        }

        @Override
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            remoteDeviceMap.put(btDevice.getBluetoothAddress(), btDevice);
            System.out.println("DEVICE: btDevice = " + btDevice + ", DeviceClass = " + cod);
            try {
                System.out.println("DEVICE: Friendly Name: " + btDevice.getFriendlyName(false));
            } catch (IOException e) {
                System.err.println("DEVICE: Couldn't get friendly name");
            }
        }

        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            System.out.println("SERVICE: transID = " + transID + ", servRecord = " + Arrays.deepToString(servRecord));
        }

        @Override
        public void serviceSearchCompleted(int transID, int respCode) {
            System.out.println("SERVICE-SEARCH-COMPL: transID = " + transID + ", respCode = " + respCode);
        }

        @Override
        public void inquiryCompleted(int discType) {
            System.out.println("INQ-COMPL: discType = " + discType);
            mutex.lock();
            try {
                inquiryCompleted.set(true);
                conditionInquiryCompleted.signal();
            } finally {
                mutex.unlock();
            }
        }
    }
}
