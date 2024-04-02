package sdis.spotify.client.unit;
import sdis.spotify.common.Primitiva;
import sdis.spotify.common.MensajeProtocolo;
import sdis.spotify.common.MalMensajeProtocoloException;
public class ReadL {
    final private int PUERTO = 2000;
    static java.io.ObjectInputStream ois = null;
    static java.io.ObjectOutputStream oos = null;
    public static void main(String[] args) throws java.io.IOException {
        String [] array=new String[3];
        array[0]="localhost";
        array[1]="hector";
        array[2]="1234";
        String host = array[0];  //localhost o ip|dn del servidor
        String usuario = array[1];  //usuario del cliente
        String clave = array[2];  //contraseña del cliente

        java.net.Socket sock = new java.net.Socket(host, 2000);
        try {
            oos = new java.io.ObjectOutputStream(sock.getOutputStream());
            ois = new java.io.ObjectInputStream(sock.getInputStream());
            MensajeProtocolo minfo = (MensajeProtocolo) ois.readObject();
            System.out.println(minfo);
            pruebaPeticionRespuesta(new MensajeProtocolo(Primitiva.READL,
                    clave));
        } catch (java.io.EOFException e) {
            System.err.println("Cliente: Fin de conexión.");
        } catch (java.io.IOException e) {
            System.err.println("Cliente: Error de apertura o E/S sobre objetos: "+e);
        } catch (MalMensajeProtocoloException e) {
            System.err.println("Cliente: Error mensaje Protocolo: "+e);
        } catch (Exception e) {
            System.err.println("Cliente: Excepción. Cerrando Sockets: "+e);
        } finally {
            ois.close();
            oos.close();
            sock.close();
        }
    }
    static void pruebaPeticionRespuesta(MensajeProtocolo mp) throws
            java.io.IOException,
            MalMensajeProtocoloException, ClassNotFoundException {
        System.out.println("> "+mp);
        oos.writeObject(mp);
        System.out.println("< "+(MensajeProtocolo) ois.readObject());
    }
}
