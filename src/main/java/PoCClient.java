import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class PoCClient {

    public void openConnection(String address) throws IOException {
        // Tries to open the connection.
        StreamConnection connection = (StreamConnection) Connector.open(address);
        if (connection == null) {
            System.err.println("Could not open connection to address: " + address);
            System.exit(1);
        }

        System.out.println("Opened connection to " + address);

        // Initializes the streams.
        try (OutputStream output = connection.openOutputStream();
            InputStream inReader = connection.openInputStream()) {

            // Starts the listening service for incoming messages.
            byte[] fromServer = inReader.readNBytes(50);
            System.out.println("Read from server " + StandardCharsets.UTF_8.decode(ByteBuffer.wrap(fromServer)));

            System.out.println("Responding...");
            output.write("hello from client".getBytes(StandardCharsets.UTF_8));
            output.flush();

        } finally {
            connection.close();
        }
    }

}
