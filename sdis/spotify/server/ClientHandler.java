package sdis.spotify.server;

import sdis.spotify.common.*;
import sdis.spotify.utils.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {
    private static final ConcurrentHashMap<String, String> userDb = new ConcurrentHashMap<>();
    private static final ConnectionManager connectionManager = new ConnectionManager(4);
    private static final ConnectionManager loginManager = new ConnectionManager(3);
    private static final AtomicInteger handlerIdCounter = new AtomicInteger();

    static {
        userDb.put("hector", "1234");
        userDb.put("sdis", "asdf");
    }

    private final Socket socket;
    private final ConcurrentMultiMap<String, String> map;
    private final int handlerId;
    private boolean isAuthenticated = false;

    public ClientHandler(Socket socket, ConcurrentMultiMap<String, String> map) {
        this.socket = socket;
        this.map = map;
        this.handlerId = handlerIdCounter.getAndIncrement();
    }

    @Override
    public void run() {
        String clientIp = socket.getInetAddress().getHostAddress();

        try (
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            oos.flush(); // Critical to prevent ObjectInputStream deadlocks

            connectionManager.registerAttempt(clientIp);
            if (connectionManager.isIpBanned(clientIp)) {
                oos.writeObject(new ProtocolMessage(ProtocolPrimitive.ERROR, ServerMessages.MAX_CONNECTIONS_REACHED_ERROR));
                return;
            }

            oos.writeObject(new ProtocolMessage(ProtocolPrimitive.INFO, ServerMessages.WELCOME_MESSAGE));

            while (true) {
                ProtocolMessage request = (ProtocolMessage) ois.readObject();
                ProtocolMessage response = processRequest(request, clientIp);
                oos.writeObject(response);
                
                // Drop connection if banned during login
                if (response.getPrimitive() == ProtocolPrimitive.ERROR) break;
            }
        } catch (Exception e) {
            System.out.println("Handler [" + handlerId + "] disconnected.");
        } finally {
            connectionManager.registerDisconnect(clientIp);
        }
    }

    private ProtocolMessage processRequest(ProtocolMessage req, String clientIp) throws Exception {
        switch (req.getPrimitive()) {
            case XAUTH:
                if (userDb.containsKey(req.getQueueId()) && userDb.get(req.getQueueId()).equals(req.getMessage())) {
                    isAuthenticated = true;
                    return new ProtocolMessage(ProtocolPrimitive.XAUTH, ServerMessages.USER_LOGGED_SUCCESSFULLY);
                } else {
                    loginManager.registerAttempt(clientIp);
                    if (loginManager.isIpBanned(clientIp)) {
                        return new ProtocolMessage(ProtocolPrimitive.ERROR, ServerMessages.MAX_LOGIN_ATTEMPTS_REACHED_ERROR);
                    }
                    return new ProtocolMessage(ProtocolPrimitive.NOTAUTH, ServerMessages.LOGIN_ERROR);
                }
            case ADD2L:
                if (!isAuthenticated) return new ProtocolMessage(ProtocolPrimitive.NOTAUTH, ServerMessages.LOGIN_REQUIRED);
                map.push(req.getQueueId(), req.getMessage());
                return new ProtocolMessage(ProtocolPrimitive.ADDED);
            case READL:
                String song = map.pop(req.getQueueId());
                return (song != null) ? new ProtocolMessage(ProtocolPrimitive.MEDIA, song) : new ProtocolMessage(ProtocolPrimitive.EMPTY);
            case DELETEL:
                if (!isAuthenticated) return new ProtocolMessage(ProtocolPrimitive.NOTAUTH, ServerMessages.LOGIN_REQUIRED);
                boolean deleted = false;
                while (map.pop(req.getQueueId()) != null) deleted = true;
                return deleted ? new ProtocolMessage(ProtocolPrimitive.DELETED) : new ProtocolMessage(ProtocolPrimitive.EMPTY);
            default:
                return new ProtocolMessage(ProtocolPrimitive.ERROR, "Unknown Command");
        }
    }
}