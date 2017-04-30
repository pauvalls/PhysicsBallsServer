/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.physicballs.items.*;

/**
 *
 * @author Liam-Portatil
 */
public class PhysicBallsServer {

    /**
     * Global parameters
     */
    private static final int PORT = 11111;
    private MapaVirtual mapa = null;
    private ObjectOutputStream out;
    private Thread listener;
    private static DatagramSocket bcListener;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PhysicBallsServer();
    }

    /**
     * Public constructor
     */
    public PhysicBallsServer() {

        try {
            /**
             * Test dummy
             */
            mapa = new MapaVirtual(2, 2);

            /**
             * Local variables
             */
            ServerSocket serverSock = new ServerSocket(PORT);
            Socket clientSock;
            String cliAddr;
            initListener();
            /**
             * While server alive
             */
            while (true) {
                System.out.println("Waiting for a client...");
                /**
                 * Open event that waits for socket petitions
                 */
                clientSock = serverSock.accept();

                cliAddr = clientSock.getInetAddress().getHostAddress();
                System.out.println("Client in: " + cliAddr);
                /**
                 * Clients Streams
                 */
                ObjectInputStream in = new ObjectInputStream(clientSock.getInputStream());
                out = new ObjectOutputStream(clientSock.getOutputStream());

                /**
                 * First client petition for registering type of client
                 */
                Object o = in.readObject();
                if (o instanceof String) {
                    String type = (String) o;
                    /**
                     * Switch that determines type of client and how to treat it
                     */
                    switch (type.toLowerCase()) {
                        case "modulo_visual":
                            /**
                             * Creates new Modulo visual client with a thread
                             * and delivers a response
                             */
                            ModuloVisualThread mvt = new ModuloVisualThread(clientSock, cliAddr, in, out, mapa);
                            Peticion reg = new Peticion("update_walls");
                            reg.pushData(new Status(1, "Ok"));
                            reg.pushData(mapa.push(mvt));
                            out.writeObject(reg);
                            break;
                        case "server_controller":
                            /**
                             * Delivers an ok State indicating that it can send
                             * a petition object
                             */
                            out.writeObject(new Status(1, "Ok"));
                            new ServerControllerThread(clientSock, cliAddr, in, out, mapa);
                            break;
                        case "client_controller":
                            /**
                             * Delivers an ok State indicating that it can send
                             * a petition object
                             */
                            out.writeObject(new Status(1, "Ok"));
                            new ClientControllerThread(clientSock, cliAddr, in, out, mapa);
                            break;
                        default:
                            out.writeObject(new Status(501, "Wrong type of client"));
                    }
                } else {
                    out.writeObject(new Status(501, "Wrong type of client - wrong value"));
                }
            }
        } catch (Exception e) {
            /**
             * General try catch
             */
            System.out.println(e);
            try {
                out.writeObject(new Status(500, "General error, contact the Administrator"));
            } catch (IOException ex) {

            }

        }
    }

    private final void initListener() {

        this.listener = new Thread(new Runnable() {
            private DatagramSocket bcListener;

            @Override
            public void run() {
                try {
                    this.bcListener = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                    this.bcListener.setBroadcast(true);
                    while (true) {
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        try {
                            this.bcListener.receive(packet);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        String message = new String(packet.getData()).trim();
                        if (message.equals("/ping")) {
                            byte[] sendData;
                            sendData = "/ping".getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            try {
                                this.bcListener.send(sendPacket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
        this.listener.start();
    }
}
