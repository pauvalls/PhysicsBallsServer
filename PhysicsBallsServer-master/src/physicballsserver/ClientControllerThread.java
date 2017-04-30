/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.physicballs.items.Ball;
import org.physicballs.items.Peticion;
import org.physicballs.items.Status;
import org.physicballs.items.AndroidBall;

/**
 *
 * @author Liam-Portatil
 */
public class ClientControllerThread extends ClientThread {

    MapaVirtual mapa;

    public ClientControllerThread(Socket s, String cliAddr, ObjectInputStream in, ObjectOutputStream out, MapaVirtual mapa) {
        super(s, cliAddr);
        this.in = in;
        this.out = out;
        this.mapa = mapa;
        this.start();
    }

    /**
     * Client thread cycle
     */
    @Override
    public void run() {
        try {
            processClient(in, out);
            clientSock.close();
        } catch (Exception e) {

        }
    }

    /**
     * Out to process client streams
     *
     * @param in
     * @param out
     */
    private void processClient(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            while (live) {
                /**
                 * Receive petition
                 */

      Object o = (Peticion) in.readObject();
                if (o instanceof Peticion) {
                    peticion = (Peticion) o;
                    switch (peticion.getAccion().toLowerCase()) {
                        case "echo":
                            out.writeObject(new Status(2, (String) peticion.getObject(0)));
                            break;
                        case "get_windows":
                            Peticion p = new Peticion("get_windows");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(mapa.getWindows());
                            out.writeObject(p);
                            break;
                        case "enviar_pelota":
                            try {
                                AndroidBall baux=(AndroidBall) peticion.getObject(0);
                              Ball  b= new Ball(baux.getX(), baux.getY(), baux.getSpeed(), baux.getAccel(), baux.getRadius(), baux.getMass(),baux.getAngle(), baux.getType());
         
                                mapa.addBall((int[]) peticion.getObject(1), b);
                                System.out.println("Pelota enviada ^^");
                            } catch (Exception e) {
                                out.writeObject(new Status(506, "Out of bounds"));
                            }
                            break;
                        default:
                            out.writeObject(new Status(505, "Petition - nonexistent option"));
                    }
                } else {
                    out.writeObject(new Status(505, "Petition - wrong type"));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            out.writeObject(new Status(503, "Error with the petition"));
        }
    }
}
