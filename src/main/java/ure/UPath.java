package ure;

import ure.terrain.URETerrain;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class UPath {

    static class Nodelist extends TreeSet<Node> {
        public Nodelist() {
            super(new Node(0,0));
        }
        public Node getLeast() {
            Node least = first();
            remove(least);
            return least;
        }
    }
    static class Node implements Comparator<Node> {
        int x, y;
        Node parent;
        double g, h, f;

        public Node(int thex, int they) {
            x = thex;
            y = they;
        }

        public Node(int thex, int they, Node theparent) {
            x = thex;
            y = they;
            parent = theparent;
        }
        public void recalc(int goalx, int goaly) {
            g = parent.g + 1;
            h = Math.abs(x - goalx) + Math.abs(y - goaly);
            f = g + h;
        }
        public double getval() {
            return f;
        }
        public int compare(Node n1, Node n2) {
            if (n1.getval() > n2.getval())
                return 1;
            return -1;
        }
    }

    public static Node NodeIfOpen(UREArea area, int x, int y, Node parent, String[] terrains) {
        if (x < 0 || y < 0 || x >= area.xsize || y >= area.ysize)
            return null;
        URETerrain t = area.terrainAt(x,y);
        for (int i=0;i<terrains.length;i++)
            if (terrains[i] == t.name)
                return new Node(x,y,parent);
        return null;
    }

    public static int[] nextStep(UREArea area, int x1, int y1, int x2, int y2, String[] terrains) {
        Nodelist openlist = new Nodelist();
        Nodelist closedlist = new Nodelist();
        Node start = new Node(x1,y1);
        openlist.add(start);
        while (!openlist.isEmpty()) {
            Node q = openlist.getLeast();
            Node[] steps = new Node[4];
            steps[0] = NodeIfOpen(area, x1-1, y1, q, terrains);
            steps[1] = NodeIfOpen(area, x1+1, y1, q, terrains);
            steps[2] = NodeIfOpen(area, x1, y1-1, q, terrains);
            steps[3] = NodeIfOpen(area, x1, y1+1, q, terrains);
            for (int i=0;i<4;i++) {
                Node step = steps[i];
                if (step != null) {
                    if (step.x == x2 && step.y == y2) {
                        // we made it!
                        while (step.parent.x != x1 && step.parent.y != y1) {
                            step = step.parent;
                        }
                        return new int[]{step.x, step.y};
                    }
                    step.recalc(x2,y2);
                    boolean skipstep = false;
                    Iterator<Node> openi = openlist.iterator();
                    while (openi.hasNext()) {
                        Node o = openi.next();
                        if (o.x == step.x && o.y == step.y && o.f < step.f) {
                            skipstep = true;
                        }
                    }
                    Iterator<Node> closedi = closedlist.iterator();
                    while (closedi.hasNext()) {
                        Node c = closedi.next();
                        if (c.x == step.x && c.y == step.y && c.f < step.f) {
                            skipstep = true;
                        }
                    }
                    if (!skipstep) {
                        openlist.add(step);
                    }
                }
            }
            closedlist.add(q);
        }
        return new int[]{0,0};
    }
}
