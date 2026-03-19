package sdis.spotify.server;

import sdis.spotify.utils.ConcurrentMultiMap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpotifyServer {
    private static final int PORT = 2000;
    private static final int THREADS = 5;

    public static void main(String[] args) {
        ConcurrentMultiMap<String, String> playlistMap = new ConcurrentMultiMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);

        System.out.println("SERVER: [STARTING] on Port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, playlistMap));
            }
        } catch (IOException e) {
            System.err.println("SERVER: [FATAL ERROR] " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}