import java.io.*;
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
                System.out.println(client.getRemoteSocketAddress());
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
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                oos = new ObjectOutputStream(this.socket.getOutputStream());
                ois = new ObjectInputStream(this.socket.getInputStream());
                oos.writeUTF("Connection accepted.");
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            label:
            while (!socket.isClosed() && socket.isConnected()) {
                try {
                    assert ois != null;
                    String clientMessage = ois.readUTF();
                    switch (clientMessage) {
                        case "pingTest":
                            oos.writeUTF("message received");
                            oos.flush();
                            break;
                        case "downloadTest":

                            break;
                        case "uploadTest":

                            break;
                        case "closeConnection":
                            break label;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}