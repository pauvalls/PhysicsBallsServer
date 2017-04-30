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
import org.physicballs.items.*;

/**
 *
 * @author Liam-Portatil
 */
public class ModuloVisualThread extends ClientThread {

    /**
     * Global parameters
     */
    String type = "Modulo visual";
    MapaVirtual mapa;

    /**
     * Constructor
     *
     * @param s
     * @param cliAddr
     * @param in
     * @param out
     */
    public ModuloVisualThread(Socket s, String cliAddr, ObjectInputStream in, ObjectOutputStream out, MapaVirtual mapa) {
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
                        case "enviar_pelota":
                            mapa.move(this, (Walls.wall) peticion.getObject(1), (Ball) peticion.getObject(0));
                            break;
                        default:
                            out.writeObject(new Status(504, "NonExistent action"));
                    }
                    out.writeObject(new Status(1, "Ok"));
                } else {
                    out.writeObject(new Status(505, "Petition - wrong value"));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            out.writeObject(new Status(503, "Error with the petition"));
        }
    }
}
