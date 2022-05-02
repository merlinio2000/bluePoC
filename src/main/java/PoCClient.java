import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PoCClient {

    public void openConnection(String address) throws IOException {
        // Tries to open the connection.
        StreamConnection connection = (StreamConnection) Connector.open(address);
        if (connection == null) {
            System.err.println("Could not open connection to address: " + address);
            System.exit(1);
        }

        System.out.println("Opened connection to " + address);


        new ReadThread(connection).start();
        new WriterThread(connection).start();

        // TODO connection possibly gets leaked
    }

}
