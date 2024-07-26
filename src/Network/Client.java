package Network;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public Board sendMove(Move move) throws IOException, ClassNotFoundException {
        out.writeObject(move);
        Object response = in.readObject();
        if (response instanceof Board) {
            return (Board) response;
        } else {
            System.out.println(response);
            return null;
        }
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 12345);
            client.connect();
            // Example move, replace with actual move logic
            Move move = null; // Replace with actual move
            Board board = client.sendMove(move);
            if (board != null) {
                System.out.println("Move successful, new board state received.");
            }
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}