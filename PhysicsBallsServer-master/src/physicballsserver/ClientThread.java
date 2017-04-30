/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.physicballs.items.Peticion;

/**
 *
 * @author Liam-Portatil
 */
public class ClientThread extends Thread {

    /**
     * Global parameters
     */
    protected Socket clientSock;
    protected String cliAddr;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    protected boolean live = true;
    protected Peticion peticion;

    public ClientThread(Socket s, String cliAddr) {
        clientSock = s;
        this.cliAddr = cliAddr;
        System.out.println("Client connection from " + cliAddr);
    }

    public void terminate() {
        this.live = false;
    }
}
