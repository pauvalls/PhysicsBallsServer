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
import org.physicballs.items.Peticion;
import org.physicballs.items.Status;

/**
 *
 * @author Liam-Portatil
 */
public class ServerControllerThread extends ClientThread {

    MapaVirtual mapa;
    public ServerControllerThread(Socket s, String cliAddr, ObjectInputStream in, ObjectOutputStream out, MapaVirtual mapa) {
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
                        case "open_map":
                            mapa = new MapaVirtual((int)peticion.getObject(0), (int)peticion.getObject(1));
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "echo":
                            out.writeObject(new Status(2, (String)peticion.getObject(0)));
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
