import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: Orkun Doğan
 * @date: 26/04/2021
 */
public class ConnectionCheckerServer {
    private final int serverSocketNumber = 9000;
    private ServerSocket serverSocket;
    private MultiClientHandler multiClientHandler;

    public ConnectionCheckerServer() {
        try {
            this.serverSocket = new ServerSocket(serverSocketNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        run();
    }

    public void run() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                this.multiClientHandler = new MultiClientHandler(client);
                this.multiClientHandler.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MultiClientHandler implements Runnable {
        Socket socket;

        public MultiClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());
                oos.writeObject("Connection accepted.");
                oos.flush();
                oos.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}