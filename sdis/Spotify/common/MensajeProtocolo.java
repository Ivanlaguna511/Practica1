package sdis.Spotify.common;

public class MensajeProtocolo implements java.io.Serializable {
    private final Primitiva primitiva;
    private final String mensaje;
    private final String idCola;

    // ADDED, EMPTY, DELETED
    public MensajeProtocolo(Primitiva p) throws MalMensajeProtocoloException {
        if (p == Primitiva.ADDED || p == Primitiva.EMPTY || p == Primitiva.DELETED) {
            this.primitiva = p;
            this.mensaje = this.idCola = null;
        } else {
            throw new MalMensajeProtocoloException();
        }
    }

    // XAUTH, MEDIA, ERROR, NOTAUTH, INFO, READL, DELETEL
    public MensajeProtocolo(Primitiva p, String mensaje) throws MalMensajeProtocoloException {
        if (p == Primitiva.XAUTH || p == Primitiva.MEDIA || p == Primitiva.ERROR || p == Primitiva.NOTAUTH) {
            this.primitiva = p;
            this.mensaje = mensaje;
            this.idCola = null;
        } else if (p == Primitiva.INFO || p == Primitiva.READL || p == Primitiva.DELETEL) {
            this.primitiva = p;
            this.mensaje = null;
            this.idCola = mensaje;
        } else {
            throw new MalMensajeProtocoloException();
        }
    }
    // XAUTH, ADD2L
    public MensajeProtocolo(Primitiva p, String user, String pass) throws MalMensajeProtocoloException {
        if (p == Primitiva.XAUTH || p == Primitiva.ADD2L) {
            this.primitiva = p;
            this.mensaje = user;
            this.idCola = pass;
        } else {
            throw new MalMensajeProtocoloException();
        }
    }

    public Primitiva getPrimitiva() {
        return this.primitiva;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public String getIdCola() {
        return this.idCola;
    }

    public String toString() {
        switch (this.primitiva) {
            case INFO:
            case ADDED:
            case EMPTY:
            case DELETED:
                return this.primitiva.toString();
            case XAUTH:
            case MEDIA:
            case ERROR:
            case NOTAUTH:
                return this.primitiva + ":" + this.mensaje;
            case READL:
            case DELETEL:
            case ADD2L:
                return this.primitiva + ":" + this.mensaje + ":" + this.idCola;
            default:
                return this.primitiva.toString();
        }
    }
}
