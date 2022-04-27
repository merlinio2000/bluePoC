import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

public class PoCService {

    //public static final UUID serviceUUID = UUID.fromString("28cfa891-e066-4184-b67e-837a6f310815");

    public static final UUID serviceUUID = new UUID("28cfa891e0664184b67e837a6f310815", false);
    public static final String serviceURL = "btspp://localhost:" + serviceUUID.toString() + ";name=BluePoC";

    public StreamConnection waitForConnection() throws IOException {
        //LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);

        StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(serviceURL);

        StreamConnection connection = notifier.acceptAndOpen();
        return connection;
    }
}
