package sdis.server;

import sdis.common.ConnectionManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runnable worker that handles the raw socket authentication protocol for a single client.
 */
public class ClientAuthHandler implements Runnable {
    // Shared global state for all threads
    private static final ConcurrentHashMap<String, String> userDatabase = new ConcurrentHashMap<>();
    private static final ConnectionManager connectionManager = new ConnectionManager(3);
    private static final ConnectionManager loginManager = new ConnectionManager(2);

    static {
        // Mock Database Initialization
        userDatabase.put("hector", "1234");
        userDatabase.put("sdis", "asdf");
    }

    private final Socket socket;

    public ClientAuthHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientIp = socket.getInetAddress().getHostAddress();
        String lastAttemptedUser = "No user logged in";

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream())
        ) {
            // 1. Connection Rate Limiting
            connectionManager.registerAttempt(clientIp);
            System.out.printf("[BM] Connections for %s = %d%n", clientIp, connectionManager.getAttempts(clientIp));

            if (connectionManager.isIpBanned(clientIp)) {
                out.println("Err Max Number of connections reached.");
                return; // Drops the connection gracefully
            }

            out.println("Welcome, please type your credentials to LOG in");

            // 2. Authentication Loop
            boolean sessionActive = true;
            while (sessionActive) {
                String username = in.readLine();
                if (username == null) break; // Client unexpectedly disconnected

                lastAttemptedUser = username;
                out.println(username);
                out.println("OK: password?");

                String password = in.readLine();
                if (password == null) break;

                out.println(password);

                if (authenticate(username, password)) {
                    out.println("User successfully logged in");
                    sessionActive = false; // Ends flow successfully
                } else {
                    loginManager.registerAttempt(clientIp);
                    System.out.printf("[BM] Login fails for %s = %d%n", clientIp, loginManager.getAttempts(clientIp));

                    if (loginManager.isIpBanned(clientIp)) {
                        out.println("Err Max Number of login attempts reached.");
                        sessionActive = false; // Bans and ends flow
                    } else {
                        out.println("Credentials do not match our records. Enter username again:");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("SERVER: Networking error with " + clientIp + " - " + e.getMessage());
        } finally {
            System.out.println("SERVER: Thread finished. Last attempted user: " + lastAttemptedUser);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private boolean authenticate(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }
}