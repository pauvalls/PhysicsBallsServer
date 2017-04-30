/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.physicballs.items.*;

/**
 *
 * @author Liam-Portatil
 */
public class MapaVirtual {

    int width = 0;
    int height = 0;
    int currentWidth = -1;
    int currentHeight = -1;
    ModuloVisualThread[][] visuales;

    public MapaVirtual(int width, int height) {
        this.width = width;
        this.height = height;
        visuales = new ModuloVisualThread[height][width];
        clean();
    }

    private void clean() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                visuales[i][j] = null;
            }
        }
    }

    public ArrayList<Walls.wall> push(ModuloVisualThread mvt) {
        boolean found = false;
        ArrayList<Walls.wall> w = null;
        for (int i = 0; i < height && !found; i++) {
            for (int j = 0; j < width && !found; j++) {
                if (visuales[i][j] == null) {
                    currentWidth = j;
                    currentHeight = i;
                    found = true;
                    visuales[i][j] = mvt;
                    w = getAvailableWalls(j, i);
                    update(j, i);
                }

            }
        }
        if (!found) {
            System.out.println("No capacity");
        }
        return w;
    }

    private ArrayList<Walls.wall> getAvailableWalls(int x, int y) {
        ArrayList<Walls.wall> w = new ArrayList<Walls.wall>();
        //left
        if (x - 1 >= 0 && visuales[y][x - 1] != null) {
            w.add(Walls.wall.LEFT);
        }
        //right
        if (x + 1 < width && visuales[y][x + 1] != null) {
            w.add(Walls.wall.RIGHT);
        }
        //up
        if (y - 1 >= 0 && visuales[y - 1][x] != null) {
            w.add(Walls.wall.TOP);
        }
        //down
        if (y + 1 < height && visuales[y + 1][x] != null) {
            w.add(Walls.wall.BOTTOM);
        }
        return w;
    }

    public void update(int x, int y) {
        Peticion p;
        //left
        try {
            if (x - 1 >= 0 && visuales[y][x - 1] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.RIGHT);
                visuales[y][x - 1].out.writeObject(p);
            }
            //right
            if (x + 1 < width && visuales[y][x + 1] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.LEFT);
                visuales[y][x + 1].out.writeObject(p);
            }
            //up
            if (y - 1 >= 0 && visuales[y - 1][x] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.BOTTOM);
                visuales[y - 1][x].out.writeObject(p);
            }
            //down
            if (y + 1 < height && visuales[y + 1][x] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.TOP);
                visuales[y + 1][x].out.writeObject(p);
            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void move(ModuloVisualThread mvt, Walls.wall w, Ball b) {
        boolean found = false;
        Peticion p;
        try {
            for (int i = 0; i < height && !found; i++) {
                for (int j = 0; j < width && !found; j++) {
                    if (visuales[i][j] == mvt) {
                        found = true;
                        if (w == Walls.wall.TOP) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            visuales[i - 1][j].out.writeObject(p);
                        }
                        if (w == Walls.wall.BOTTOM) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            visuales[i + 1][j].out.writeObject(p);
                        }
                        if (w == Walls.wall.RIGHT) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            visuales[i][j + 1].out.writeObject(p);
                        }
                        if (w == Walls.wall.LEFT) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            visuales[i][j - 1].out.writeObject(p);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addBall(int[] window, Ball b) throws IOException {
        Peticion p = new Peticion("addBall");
        p.pushData(new Status(1, "Ok"));
        p.pushData(b);
        visuales[window[1]][window[0]].out.writeObject(p);
    }

    public int[] getWindows() {
        int[] aux = {currentWidth, currentHeight};
        return aux;
    }
}
