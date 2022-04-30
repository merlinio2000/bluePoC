
import javax.bluetooth.*;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

        System.err.println("LOCAL: " + localDevice.getBluetoothAddress());
        System.err.flush();

        //new Thread(() -> startService()).run();
        //startService();

        DiscoveryAgent discoverAgent = localDevice.getDiscoveryAgent();

        // var devices = PoCDiscoverer.searchDevicesSynchronous(discoverAgent);
        /*
        if (devices.containsKey(localDevice.getBluetoothAddress())) {
            System.err.println("GOTUS");
        }

        PoCDiscoverer.searchServicesOnDeviceSynchronous(discoverAgent, devices.get("A0AFBD29A567"));
        */
        System.out.println("Attempting to connect...");
        var client = new PoCClient();
        final String srvAddr = "A0AFBD29A567";
        final String srvUUID = PoCService.serviceUUID.toString();
        client.openConnection("btspp://%s:%s".formatted(srvAddr, "4"));

        /*
        for (var device : devices.values()) {
            PoCDiscoverer.searchServicesOnDeviceSynchronous(discoverAgent, device);
        }
        */

    }


    public static void startService() throws IOException {
        var serviceFactory = new PoCService();
        byte[] text = "Blue World".getBytes(StandardCharsets.UTF_8);

        StreamConnection clientConn = serviceFactory.waitForConnection();
        System.out.println("Connection opened!");
        var clientDevice = RemoteDevice.getRemoteDevice(clientConn);
        if (!clientDevice.isAuthenticated()) {
            System.err.println("Client not authenticated");
        }
        InputStream fromClient = clientConn.openInputStream();
        OutputStream toClient = clientConn.openOutputStream();
        try {
            Runnable writeThread = () -> {
                try {
                    toClient.write(text);
                    toClient.flush();

                    Scanner input = new Scanner(System.in);
                    String toSend;
                    while (!(toSend = input.nextLine()).equals("EXIT")) {
                        System.out.println("Sending to client:" + toSend);
                        toClient.write(toSend.getBytes(StandardCharsets.UTF_8));
                        toClient.flush();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        toClient.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            new Thread(writeThread).start();

            Runnable readThread = () -> {
                int readCnt = 0;
                while (readCnt != -1) {
                    byte[] received = new byte[50];
                    try {
                        fromClient.read(received);
                        System.out.println("From client: " + StandardCharsets.UTF_8.decode(ByteBuffer.wrap(received)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            new Thread(readThread).start();

        } finally {
            //clientConn.close();
        }
    }
}
