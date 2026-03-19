package sdis.spotify.client;

import sdis.spotify.common.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SpotifyInteractiveClient {
    private static final String HOST = "localhost";
    private static final int PORT = 2000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            oos.flush();
            ProtocolMessage welcomeMsg = (ProtocolMessage) ois.readObject();
            System.out.println("SERVER: " + welcomeMsg);

            if (welcomeMsg.getPrimitive() == ProtocolPrimitive.ERROR) return;

            while (true) {
                System.out.println("\n--- SPOTIFY MENU ---");
                System.out.println("1. Login");
                System.out.println("2. Add Song to Playlist");
                System.out.println("3. Read Playlist");
                System.out.println("4. Delete Playlist");
                System.out.println("5. Exit");
                System.out.print("Select an option: ");
                
                String option = scanner.nextLine();
                ProtocolMessage request = null;

                try {
                    switch (option) {
                        case "1":
                            System.out.print("User: "); String user = scanner.nextLine();
                            System.out.print("Pass: "); String pass = scanner.nextLine();
                            request = new ProtocolMessage(ProtocolPrimitive.XAUTH, user, pass);
                            break;
                        case "2":
                            System.out.print("Playlist Name: "); String pList = scanner.nextLine();
                            System.out.print("Song Name: "); String song = scanner.nextLine();
                            request = new ProtocolMessage(ProtocolPrimitive.ADD2L, pList, song);
                            break;
                        case "3":
                            System.out.print("Playlist Name: ");
                            request = new ProtocolMessage(ProtocolPrimitive.READL, scanner.nextLine());
                            break;
                        case "4":
                            System.out.print("Playlist Name: ");
                            request = new ProtocolMessage(ProtocolPrimitive.DELETEL, scanner.nextLine());
                            break;
                        case "5": return;
                        default: System.out.println("Invalid option."); continue;
                    }

                    System.out.println(">> Sending: " + request);
                    oos.writeObject(request);
                    ProtocolMessage response = (ProtocolMessage) ois.readObject();
                    System.out.println("<< Received: " + response);

                    if (response.getPrimitive() == ProtocolPrimitive.ERROR) break;

                } catch (IllegalArgumentException e) {
                    System.out.println("Local Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("CLIENT FATAL ERROR: " + e.getMessage());
        }
    }
}