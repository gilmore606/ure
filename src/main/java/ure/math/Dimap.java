package ure.math;

import ure.actors.UActor;
import ure.areas.UArea;
import ure.sys.UCommander;
import ure.terrain.UTerrain;

import java.util.ArrayList;

public class Dimap {

    UArea area;
    UCommander commander;
    static int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{1,1},{-1,1},{1,-1}};

    float[][] map;
    int[][] edges;
    int[][] newedges;
    int edgei, newedgei;

    int updateTurn = 0;

    ArrayList<int[]> targets;

    int targetx,targety;  // this is mostly for test right now

    public Dimap(UArea area, UCommander commander) {
        this.area = area;
        this.commander = commander;
        map = new float[area.xsize][area.ysize];
        edges = new int[(area.xsize+area.ysize)*3][2];
        newedges = new int[(area.xsize+area.ysize)*3][2];
        edgei = 0;
        newedgei = 0;
        targets = new ArrayList<>();
    }

    public float valueAt(int x, int y) {
        if (commander.turnCounter > updateTurn && updateTargets()) {
            update();
        }
        if (area.isValidXY(x,y))
            return map[x][y];
        return -1f;
    }

    public int[] stepDown(int[] pos) {
        float val = valueAt(pos[0],pos[1]);
        if (val <= 0f)
            return null;
        float min = val;
        int minxdir = 0;
        int minydir = 0;
        for (int[] dir : dirs) {
            float tv = valueAt(pos[0]+dir[0],pos[1]+dir[1]);
            if (tv >= 0f && tv < min) {
                min = tv;
                minxdir = dir[0];
                minydir = dir[1];
            }
        }
        if (minxdir == 0 && minydir == 0) return null;
        pos[0] += minxdir;
        pos[1] += minydir;
        return pos;
    }

    boolean updateTargets() {
        if (commander.player() == null) return false;
        if (commander.player().areaX() != targetx || commander.player().areaY() != targety) {
            targets.clear();
            addTarget(commander.player().areaX(),commander.player().areaY());
            return true;
        }
        return false;
    }

    void addTarget(int x, int y) {
        targets.add(new int[]{x,y});
    }

    void update() {
        for (int i=0;i<map.length;i++) {
            for (int j = 0;j < map[0].length;j++)
                map[i][j] = -1f;
        }
        edgei = 0;
        for (int[] tp : targets) {
            map[tp[0]][tp[1]] = 0f;
            edges[edgei][0] = tp[0]; edges[edgei][1] = tp[1];
            edgei++;
        }

        float step = 0f;
        while (edgei > 0) {
            newedgei = 0;
            for (int ei=0;ei<edgei;ei++) {
                int[] edge = edges[ei];
                if (map[edge[0]][edge[1]] <= step) {
                    for (int[] dir : dirs) {
                        int nx = edge[0]+dir[0];
                        int ny = edge[1]+dir[1];
                        if (nx >= 0 && ny >= 0 && nx < map.length && ny < map[0].length) {
                            if (map[nx][ny] < 0f) {
                                // TODO: make a real actor check here, this is bullshit
                                UTerrain t = area.terrainAt(nx, ny);
                                if (t != null) {
                                    if (t.isPassable()) {
                                        map[nx][ny] = map[edge[0]][edge[1]] + t.getMovespeed();
                                        newedges[newedgei][0] = nx;
                                        newedges[newedgei][1] = ny;
                                        newedgei++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int[][] tmp = edges;
            edges = newedges;
            newedges = tmp;
            edgei = newedgei;
            step = step + 1f;
        }
        updateTurn = commander.turnCounter;
    }

}
