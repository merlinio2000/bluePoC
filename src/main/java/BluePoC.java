
import javax.bluetooth.*;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BluePoC {

    public static void main(String[] args) throws IOException, InterruptedException {

        if (!LocalDevice.isPowerOn()) {
            System.err.println("Please turn on bluetooth");
            System.exit(1);
        }

        LocalDevice localDevice = LocalDevice.getLocalDevice();

        /*
        if (!localDevice.setDiscoverable(DiscoveryAgent.GIAC)) {
            System.err.println("Couldnt set discoverable");
            System.exit(2);
        };
         */

        //new Thread(() -> startService()).run();

        System.err.println("LOCAL: " + localDevice.getBluetoothAddress());

        DiscoveryAgent discoverAgent = localDevice.getDiscoveryAgent();

        var devices = PoCDiscoverer.searchDevicesSynchronous(discoverAgent);

        if (devices.containsKey(localDevice.getBluetoothAddress())) {
            System.err.println("GOTUS");
        }

        for (var device : devices.values()) {
            PoCDiscoverer.searchServicesOnDeviceSynchronous(discoverAgent, device);
        }


    }


    public static void startService() throws IOException {
        var serviceFactory = new PoCService();
        byte[] text = "Blue World".getBytes(StandardCharsets.UTF_8);

        StreamConnection clientConn = serviceFactory.waitForConnection();

        InputStream fromClient = clientConn.openInputStream();
        OutputStream toClient = clientConn.openOutputStream();

        toClient.write(text);

        byte[] received = fromClient.readAllBytes();

        System.out.println(received);

        clientConn.close();

    }



}
