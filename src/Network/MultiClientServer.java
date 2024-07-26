package Network;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServer {
    private final SocketManager socketManager;
    private final ExecutorService pool;

    public MultiClientServer(int port) throws IOException {
        this.socketManager = new SocketManager();
        this.socketManager.createServerSocket(port);
        this.pool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        System.out.println("Multi-client server started...");
        while (true) {
            try {
                Socket clientSocket = socketManager.acceptClientConnection();
                pool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Board board;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.board = Board.createStandardBoard();
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());

                while (true) {
                    Move move = (Move) in.readObject();
                    Player currentPlayer = board.currentPlayer();
                    if (currentPlayer.isMoveLegal(move)) {
                        board = currentPlayer.makeMove(move).getTransitionBoard();
                        out.writeObject(board);
                    } else {
                        out.writeObject("Illegal move");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            MultiClientServer server = new MultiClientServer(12345);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}