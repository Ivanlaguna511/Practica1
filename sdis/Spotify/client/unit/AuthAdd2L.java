package sdis.Spotify.client.unit;

import sdis.Spotify.common.MalMensajeProtocoloException;
import sdis.Spotify.common.MensajeProtocolo;
import sdis.Spotify.common.Primitiva;

import java.util.Scanner;

public class AuthAdd2L {
    final private int PUERTO = 2000;
    static java.io.ObjectInputStream ois = null;
    static java.io.ObjectOutputStream oos = null;
    public static void main(String[] args) throws java.io.IOException {
        Scanner s=new Scanner(System.in);
        String [] array=new String[5];
        array[0]="localhost";


        String host = array[0];  //localhost o ip|dn del servidor

        java.net.Socket sock = new java.net.Socket(host, 2000);
        try {
            oos = new java.io.ObjectOutputStream(sock.getOutputStream());
            ois = new java.io.ObjectInputStream(sock.getInputStream());
            MensajeProtocolo minfo = (MensajeProtocolo) ois.readObject();
            System.out.println(minfo);
            if(!minfo.getPrimitiva().equals(Primitiva.ERROR)){

                System.out.println("introduzca su nombre de usuario");
                array[1]=s.nextLine();
                System.out.println("introduzca su contraseña");
                array[2]=s.nextLine();



                String usuario = array[1];  //usuario del cliente
                String contraseña = array[2];  //contraseña del cliente



                pruebaPeticionRespuesta(new MensajeProtocolo(Primitiva.XAUTH, usuario, contraseña));
                MensajeProtocolo mensaje1=(MensajeProtocolo) ois.readObject();
                System.out.println("< " + mensaje1);

                if(mensaje1.getPrimitiva()==Primitiva.XAUTH){
                    System.out.println("introduzaca playlist");
                    array[3]=s.nextLine();
                    System.out.println("introduzca cancion");
                    array[4]=s.nextLine();

                    String clave=array[3];
                    String valor=array[4];

                    pruebaPeticionRespuesta(new MensajeProtocolo(Primitiva.ADD2L,clave,valor));
                    System.out.println("< "+(MensajeProtocolo) ois.readObject());
                }
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

    }
}
