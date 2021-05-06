import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

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
            byte[] fileContent = null;
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
                        case "downloadTest":
                            oos.writeUTF("Download message received.");
                            oos.flush();
                            File largeFile100MB = new File("/home/ec2-user/100MB.txt");
                            fileContent = Files.readAllBytes(largeFile100MB.toPath());
                            oos.writeObject(fileContent);
                            oos.flush();
                            break;
                        case "uploadTest":
                            oos.writeUTF("Upload message received.");
                            oos.flush();
                            try {
                                fileContent = (byte[]) ois.readObject();
                                long endTime = System.currentTimeMillis();
                                oos.writeLong(endTime);
                                oos.flush();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "closeConnection":
                            break label;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}