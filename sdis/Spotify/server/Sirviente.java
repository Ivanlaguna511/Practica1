package sdis.spotify.server;

import sdis.spotify.common.MalMensajeProtocoloException;
import sdis.utils.BlacklistManager;
import sdis.utils.MultiMap;
import sdis.spotify.common.Primitiva;
import sdis.spotify.common.MensajeProtocolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Sirviente implements Runnable {
    private final Socket socket;
    private final MultiMap<String, String> almacenamiento;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final int ns;
    private static ConcurrentHashMap<String, String> registroUsuarios = new ConcurrentHashMap<>();
    private static BlacklistManager managerConexiones = new BlacklistManager(3);
    private static BlacklistManager managerLogins = new BlacklistManager(2);
    private static AtomicInteger nInstance=
            new AtomicInteger();
    Sirviente(Socket s, MultiMap
            <String, String> c) throws IOException {
        this.socket = s;
        this.almacenamiento = c;
        this.ns = nInstance.getAndIncrement();
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }  //se invoca en el Servidor, usualmente
    public void run() {
        registroUsuarios.put("hector", "1234");
        registroUsuarios.put("sdis", "asdf");
        try {
            String mensaje;  //String multipurpose
            MensajeProtocolo me = (MensajeProtocolo) ois.readObject();
            MensajeProtocolo ms = null;
            boolean fin = false;
            boolean authored = false;

            String clientIP = socket.getInetAddress().getHostAddress();
            managerConexiones.registraIntento(clientIP);
            System.out.println("[BM] (connections) for " +  clientIP + " = " + managerConexiones.getIntentos(clientIP));

            // Verificar si la IP está en la lista negra de conexiones
            if (managerConexiones.isIPBaneada(clientIP)) {
                ms = new MensajeProtocolo(Primitiva.ERROR, "Err Max Number of connections reached");
                oos.writeObject(ms);
                fin = true;
            }
            while (!fin) {
                //me y ms: mensajes entrante y saliente
                System.out.println("Sirviente: "+ns+": [ME: "+ me);  //depuracion me
                switch (me.getPrimitiva()) {
                    case XAUTH:
                        String username = me.getIdCola();
                        String password = me.getMensaje();
                        if (registroUsuarios.containsKey(username) && registroUsuarios.get(username).equals(password)) {//Si el usuario y contraseña están en el multimap
                            ms = new MensajeProtocolo(Primitiva.XAUTH, "User successfully logged");
                            authored = true;
                        } else {
                            managerLogins.registraIntento(clientIP);
                            if (managerLogins.isIPBaneada(clientIP)) {
                                ms = new MensajeProtocolo(Primitiva.ERROR, "Err Max Number of login attempts reached");
                            } else {
                                ms = new MensajeProtocolo(Primitiva.NOTAUTH, "Err 401 ~ Credentials DO NOT MATCH. Try again");
                            }
                        }
                        break;
                        
                    case ADD2L:
                        if (authored){
                            almacenamiento.push(me.getIdCola(), me.getMensaje());
                            synchronized (almacenamiento) {
                                almacenamiento.notify();
                            }  //despierta un sirviente esperando en un bloqueo de "mapa"
                            ms = new MensajeProtocolo(Primitiva.ADDED);
                        }else{
                            ms = new MensajeProtocolo(Primitiva.NOTAUTH, "Err 401 ~ Credentials DO NOT MATCH. Try again");
                        }
                        break;

                    case READL:
                        if (null == (mensaje = almacenamiento.pop(me.getIdCola()))) {
                        ms = new MensajeProtocolo(Primitiva.MEDIA, mensaje);
                        } else {
                        ms = new MensajeProtocolo(Primitiva.EMPTY);
                        }
                        break;
                        
                    case DELETEL:
                        String clave = me.getIdCola();
                        if (authored){
                            if(almacenamiento.pop(clave) != null){
                                ms = new MensajeProtocolo(Primitiva.DELETED);
                            }else{
                                ms = new MensajeProtocolo(Primitiva.EMPTY);
                            }
                        }else{
                            ms = new MensajeProtocolo(Primitiva.NOTAUTH, "Err 401 ~ Credentials DO NOT MATCH. Try again");
                        }
                        break;
                        
                    case INFO:
                        ms = new MensajeProtocolo(Primitiva.INFO, "Bienvenid@, esto es Spotify");
                        break;

                    default:
                        ms = new MensajeProtocolo(Primitiva.ERROR);
                        
                }  //fin del selector segun el mensaje entrante
                oos.writeObject(ms);  //concentra la escritura de mensajes ¿bueno?
                        //depuracion de mensaje saliente
                System.out.println("Sirviente: "+ns+": [RESP: "+ms+"]");
            }
        } catch (IOException e) {
            System.err.println("Sirviente: "+ns+": [FIN]");
        } catch (ClassNotFoundException ex) {
            System.err.println("Sirviente: "+ns+": [ERR Class not found]");
        } catch (MalMensajeProtocoloException ex) {
            System.err.println("Sirviente: "+ns+": [ERR MalMensajeProtocolo ]");
        } finally {
            //seguimos deshaciéndonos de los sockets y canales abiertos.
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (Exception x) {
                System.err.println("Sirviente: "+ns+": [ERR Cerrando sockets]");
            }
        }
    }
}