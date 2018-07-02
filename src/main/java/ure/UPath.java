package ure;

import ure.terrain.URETerrain;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class UPath {

    class Nodelist extends TreeSet<Node> {
        public Nodelist() {
            super(new Node(0,0));
        }
    }
    class Node implements Comparator<Node> {
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

    public Node NodeIfOpen(UREArea area, int x, int y, Node parent, String[] terrains) {
        if (x < 0 || y < 0 || x >= area.xsize || y >= area.ysize)
            return null;
        URETerrain t = area.terrainAt(x,y);
        for (int i=0;i<terrains.length;i++)
            if (terrains[i].equals(t.name))
                return new Node(x,y,parent);
        return null;
    }

    public int mdist(int x1, int y1, int x2, int y2) {
        return Math.abs(x2-x1) + Math.abs(y2-y1);
    }

    public int[] nextStep(UREArea area, int x1, int y1, int x2, int y2, String[] terrains, int maxrange) {
        if (mdist(x1,y1,x2,y2) > maxrange)
            return new int[]{0,0};
        Nodelist openlist = new Nodelist();
        Nodelist closedlist = new Nodelist();
        Node start = new Node(x1,y1);
        openlist.add(start);
        int stepcount = 0;
        while (!openlist.isEmpty() && stepcount < 10000) {
            stepcount++;
            Node q = openlist.pollFirst();
            Node[] steps = new Node[4];
            steps[0] = NodeIfOpen(area, q.x-1, q.y, q, terrains);
            steps[1] = NodeIfOpen(area, q.x+1, q.y, q, terrains);
            steps[2] = NodeIfOpen(area, q.x, q.y-1, q, terrains);
            steps[3] = NodeIfOpen(area, q.x, q.y+1, q, terrains);
            for (int i=0;i<4;i++) {
                Node step = steps[i];
                if (step != null) {
                    if (step.x == x2 && step.y == y2) { // FOUND IT
                        while (step.parent.x != x1 && step.parent.y != y1) {
                            step = step.parent;
                        }
                        System.out.println("found path in " + Integer.toString(stepcount) + " steps");
                        return new int[]{step.x, step.y};
                    }
                    step.recalc(x2,y2);
                    boolean skipstep = false;
                    for (Node o : openlist) {
                        if (o.x == step.x && o.y == step.y && o.f < step.f) {
                            skipstep = true;
                        }
                    }
                    for (Node c : closedlist) {
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
