package sdis.spotify.common;

import java.io.Serializable;

/**
 * Immutable Data Transfer Object (DTO) for Client-Server communication.
 */
public class ProtocolMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ProtocolPrimitive primitive;
    private final String message;
    private final String queueId;

    public ProtocolMessage(ProtocolPrimitive p) throws IllegalArgumentException {
        if (p == ProtocolPrimitive.ADDED || p == ProtocolPrimitive.EMPTY || p == ProtocolPrimitive.DELETED) {
            this.primitive = p;
            this.message = null;
            this.queueId = null;
        } else throw new IllegalArgumentException("Invalid primitive for empty constructor");
    }

    public ProtocolMessage(ProtocolPrimitive p, String payload) throws IllegalArgumentException {
        this.primitive = p;
        if (p == ProtocolPrimitive.XAUTH || p == ProtocolPrimitive.MEDIA || p == ProtocolPrimitive.ERROR || p == ProtocolPrimitive.NOTAUTH) {
            this.message = payload;
            this.queueId = null;
        } else if (p == ProtocolPrimitive.INFO || p == ProtocolPrimitive.READL || p == ProtocolPrimitive.DELETEL) {
            this.message = null;
            this.queueId = payload;
        } else throw new IllegalArgumentException("Invalid primitive for single payload constructor");
    }

    public ProtocolMessage(ProtocolPrimitive p, String key, String value) throws IllegalArgumentException {
        if (p == ProtocolPrimitive.XAUTH || p == ProtocolPrimitive.ADD2L) {
            this.primitive = p;
            this.queueId = key;
            this.message = value;
        } else throw new IllegalArgumentException("Invalid primitive for key-value constructor");
    }

    public ProtocolPrimitive getPrimitive() { return primitive; }
    public String getMessage() { return message; }
    public String getQueueId() { return queueId; }

    @Override
    public String toString() {
        if (queueId != null && message != null) return primitive + ": " + queueId + " -> " + message;
        if (queueId != null) return primitive + ": " + queueId;
        if (message != null) return primitive + ": " + message;
        return primitive.toString();
    }
}