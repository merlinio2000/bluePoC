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

        // Initializes the streams.
        OutputStream toServer = connection.openOutputStream();
        InputStream fromServer = connection.openInputStream();

        Runnable readThread = () -> {
            int readCnt = 0;
            while (readCnt != -1) {
                byte[] readBytes = new byte[50];
                try {
                    readCnt = fromServer.read(readBytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Read from server " + StandardCharsets.UTF_8.decode(ByteBuffer.wrap(readBytes)));
            }
        };

        Runnable writeThread = () -> {
                System.out.println("Responding...");
                try {
                    toServer.write("hello from client".getBytes(StandardCharsets.UTF_8));
                    toServer.flush();

                    Scanner input = new Scanner(System.in);
                    String toSend;
                    while (!(toSend = input.nextLine()).equals("EXIT")) {
                        System.out.println("Sending to server:" + toSend);
                        toServer.write(toSend.getBytes(StandardCharsets.UTF_8));
                        toServer.flush();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        };

        try  {
            new Thread(readThread).start();
            new Thread(writeThread).start();
        } finally {
            //connection.close();
        }
    }

}
