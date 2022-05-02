import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class WriterThread extends Thread {

    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final StreamConnection connection;

    public WriterThread(StreamConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        OutputStream outStream;
        try {
            outStream = connection.openOutputStream();
        } catch (IOException ioException) {
            throw new RuntimeException("Couldnt get connection outputStream");
        }
        try {
            Scanner input = new Scanner(System.in);
            String toSend;
            while (!(toSend = input.nextLine()).equals("EXIT") && !terminated.get()) {
                System.out.println("Sending:" + toSend);
                outStream.write(toSend.getBytes(StandardCharsets.UTF_8));
                outStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void terminate() {
        terminated.set(true);
    }
}

