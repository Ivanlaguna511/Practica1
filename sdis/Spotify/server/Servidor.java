
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    public static final int PUERTO = 2000;
    private static ConcurrentHashMap<String, String> almacenamiento = new ConcurrentHashMap<>();
    private static BlacklistManager managerConexiones = new BlacklistManager(4);
    private static BlacklistManager managerLogins = new BlacklistManager(3);

    public static void main(String[] args) {
        almacenamiento.put("hector", "1234");
        almacenamiento.put("sdis", "asdf");
        final String[] lastLoggedInUser = {"No user logged in"};
        //PUERTO
        try (java.net.ServerSocket servidor = new java.net.ServerSocket(PUERTO, 0, java.net.InetAddress.getByName("0.0.0.0"))) {
            while (true) {
                try {
                    System.out.println("----Server waiting client----");
                    java.net.Socket sock = servidor.accept();
                    java.io.BufferedReader inred =
                            new java.io.BufferedReader(
                                    new java.io.InputStreamReader(sock.getInputStream()));
                    java.io.PrintStream outred =
                            new java.io.PrintStream(sock.getOutputStream());

                    Runnable sirviente = () -> {
                        try {
                            boolean fin = false;

                            String clientIP = sock.getInetAddress().getHostAddress();
                            managerConexiones.registraIntento(clientIP);
                            System.out.println("[BM] (connections) for " +  clientIP + " = " + managerConexiones.getIntentos(clientIP));
                            // Verificar si la IP está en la lista negra de conexiones
                            if (managerConexiones.isIPBaneada(clientIP)) {
                                outred.println("Err Max Number of connections reached.");
                                fin = true;
                            }
                            else{
                                outred.println("Welcome, please type your credentials to LOG in");
                            }

                            while (!fin) {
                                String username = inred.readLine();
                                System.out.println("Usuario: " + username);
                                lastLoggedInUser[0] = username;
                                outred.println(username);
                                outred.println("OK: password?");

                                String password = inred.readLine();
                                System.out.println("Contraseña: " + password);
                                outred.println(password);

                                // Comprobar si el par corresponde con alguno de sus registros
                                if (coincide(username, password)) {
                                    outred.println("User successfully logged in");
                                    fin = true;
                                } else {
                                    // Incrementar intentos fallidos y verificar si la IP debe ser baneada
                                    managerLogins.registraIntento(clientIP);
                                    System.out.println("[BM] (login fails) for " +  clientIP + " = " + managerLogins.getIntentos(clientIP));
                                    if (managerLogins.isIPBaneada(clientIP)) {
                                        outred.println("Err Max Number of login attempts reached.");
                                        fin = true;
                                    } else {
                                        outred.println("Credentials do not match our records. Enter username again:");

                                    }
                                }

                            }
                        } catch (java.io.IOException ioe) {
                            System.err.println("Cerrando socket de cliente");
                            ioe.printStackTrace(System.err);
                        }
                        System.out.println("Last logged in user for this thread: " + Arrays.toString(lastLoggedInUser));
                    };

                    Thread t = new Thread(sirviente, "Sirviente echo");
                    t.start();
                } catch (java.io.IOException e) {
                    System.err.println("Cerrando socket de cliente");
                    e.printStackTrace(System.err);
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Cerrando socket de servicio");
            e.printStackTrace(System.err);
        }
    }

    private static boolean coincide(String username, String password) {
        return almacenamiento.containsKey(username) && almacenamiento.get(username).equals(password);
    }
}
