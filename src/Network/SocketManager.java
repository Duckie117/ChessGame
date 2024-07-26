package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void createServerSocket(int port) throws IOException {
        int availablePort = findAvailablePort(port);
        this.serverSocket = new ServerSocket(availablePort);
        System.out.println("Server started on port: " + availablePort);
    }

    public Socket acceptClientConnection() throws IOException {
        if (serverSocket != null) {
            this.clientSocket = serverSocket.accept();
        }
        return this.clientSocket;
    }

    public void createClientSocket(String serverAddress, int serverPort) throws IOException {
        this.clientSocket = new Socket(serverAddress, serverPort);
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }

    public void closeServerSocket() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    public void closeClientSocket() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    private int findAvailablePort(int startPort) {
        int port = startPort;
        while (!isPortAvailable(port)) {
            port++;
        }
        return port;
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}