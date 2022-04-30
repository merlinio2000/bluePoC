import com.intel.bluetooth.BluetoothConsts;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

public class PoCService {

    //public static final UUID serviceUUID = UUID.fromString("28cfa891-e066-4184-b67e-837a6f310815");

    public static final UUID serviceUUID = new UUID("0000110100001000800000805f9b34fb", false);
    public static final String serviceURL = "btspp://localhost:" + serviceUUID.toString() + ";name=BluePoC;authenticate=false;authorize=false;encrypt=false";

    public StreamConnection waitForConnection() throws IOException {
        //LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);

        StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(serviceURL);

        ServiceRecord blueService = LocalDevice.getLocalDevice().getRecord(notifier);
        System.out.println("BlueService: " + blueService.getConnectionURL(0, false));
        LocalDevice.getLocalDevice().updateRecord(blueService);

        StreamConnection connection = notifier.acceptAndOpen();
        return connection;
    }
}
