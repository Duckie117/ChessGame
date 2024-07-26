package Network;

import com.chess.engine.board.Board;
import com.chess.engine.player.Player;
import com.chess.engine.board.Move;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        System.out.println("Chess server started...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
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
            Server server = new Server(12345);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}