package sdis.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Terminal-based TCP client to interact with the Authentication Server.
 */
public class AuthClient {
    private static final String HOST = "localhost"; 
    private static final int PORT = 2000;

    public static void main(String[] args) {
        System.out.println("CLIENT: Attempting to connect to " + HOST + ":" + PORT);

        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream serverOut = new PrintStream(socket.getOutputStream());
            BufferedReader keyboardIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Read initial welcome or ban message
            String serverMessage = serverIn.readLine();
            System.out.println("SERVER: " + serverMessage);

            if (serverMessage != null && serverMessage.startsWith("Err")) {
                System.out.println("CLIENT: Connection rejected by server. Exiting.");
                return; 
            }

            // Main interaction loop matching the Server's protocol
            while (true) {
                System.out.print("Username > ");
                String username = keyboardIn.readLine();
                if (username == null || username.equalsIgnoreCase("exit")) break;

                serverOut.println(username);

                // Read server's username echo and password prompt
                System.out.println("SERVER: " + serverIn.readLine());
                System.out.println("SERVER: " + serverIn.readLine());

                System.out.print("Password > ");
                String password = keyboardIn.readLine();
                serverOut.println(password);

                // Read server's password echo and final status
                System.out.println("SERVER: " + serverIn.readLine());
                String status = serverIn.readLine();
                System.out.println("SERVER: " + status);

                // Exit loop if login is successful or max attempts reached
                if (status != null && (status.contains("successfully") || status.startsWith("Err"))) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("CLIENT: Network Error - " + e.getMessage());
        }
    }
}