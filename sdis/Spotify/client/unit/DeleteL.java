package sdis.Spotify.client.unit;
import sdis.Spotify.common.Primitiva;
import sdis.Spotify.common.MensajeProtocolo;
import sdis.Spotify.common.MalMensajeProtocoloException;

import java.util.Scanner;

public class DeleteL {
    final private int PUERTO = 2000;
    static java.io.ObjectInputStream ois = null;
    static java.io.ObjectOutputStream oos = null;
    public static void main(String[] args) throws java.io.IOException {
        Scanner s = new Scanner(System.in);
        String [] array=new String[2];
        array[0]="localhost";

        String host = array[0];  //localhost o ip|dn del servidor

        java.net.Socket sock = new java.net.Socket(host, 2000);
        try {
            oos = new java.io.ObjectOutputStream(sock.getOutputStream());
            ois = new java.io.ObjectInputStream(sock.getInputStream());
            MensajeProtocolo minfo = (MensajeProtocolo) ois.readObject();
            System.out.println(minfo);
            if(!minfo.getPrimitiva().equals(Primitiva.ERROR)){
                System.out.println("introduzca la playlist que desea eliminar");
                array[1]=s.nextLine();
                String clave = array[1];
                pruebaPeticionRespuesta(new MensajeProtocolo(Primitiva.DELETEL,
                        clave));
            }

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
