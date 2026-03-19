package sdis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main entry point for the Authentication Server.
 * Listens for incoming TCP connections and assigns them to a managed Thread Pool.
 */
public class AuthServer {
    private static final int PORT = 2000;
    private static final int THREAD_POOL_SIZE = 10; // Limits concurrent threads to save RAM

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        System.out.println("SERVER: [STARTING] Listening on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER: New connection from " + clientSocket.getInetAddress().getHostAddress());
                
                // Delegate the complex logic to a dedicated handler
                threadPool.execute(new ClientAuthHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("SERVER: [CRITICAL ERROR] Failed to bind port: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}