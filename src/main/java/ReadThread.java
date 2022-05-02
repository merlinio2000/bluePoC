import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadThread extends Thread {

    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final StreamConnection connection;

    public ReadThread(StreamConnection streamConnection) {
        connection = streamConnection;
    }


    @Override
    public void run() {

        final InputStream fromServer;

        try {
            fromServer = connection.openInputStream();
        } catch (IOException ioE) {
            throw new RuntimeException(ioE);
        }

        try {
            int readCnt = 1;
            while (readCnt > 0 && !terminated.get()) {
                byte[] readBytes = new byte[50];
                try {
                    readCnt = fromServer.read(readBytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Received:" + StandardCharsets.UTF_8.decode(ByteBuffer.wrap(readBytes)));
            }
        } finally {
            try {
                fromServer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void terminate() {
        terminated.set(true);
    }
}
