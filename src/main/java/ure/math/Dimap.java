package ure.math;

import com.google.common.eventbus.EventBus;
import ure.areas.UArea;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.terrain.UTerrain;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;

public class Dimap {

    @Inject
    public UCommander commander;
    @Inject
    public EventBus bus;

    public boolean dirty;

    UArea area;
    int type;
    HashSet<String> moveTypes;

    static int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{1,1},{-1,1},{1,-1}};
    public static int TYPE_SEEK = 0;
    public static int TYPE_FLEE = 1;

    float[][] map;
    int[][] edges;
    int[][] newEdges;
    int edgeI, newEdgeI;
    ArrayList<int[]> targets;

    int updateTurn = 0;

    public Dimap(UArea area, int type, HashSet<String> moveTypes) {
        Injector.getAppComponent().inject(this);
        this.area = area;
        this.type = type;
        this.moveTypes = moveTypes;
        map = new float[area.xsize][area.ysize];
        edges = new int[(area.xsize+area.ysize)*3][2];
        newEdges = new int[(area.xsize+area.ysize)*3][2];
        edgeI = 0;
        newEdgeI = 0;
        dirty = false;
        targets = new ArrayList<>();
    }

    public boolean targetsChanged() {
        return false;
    }

    public void updateTargets() {

    }

    public float valueAt(int x, int y) {
        if (dirty || (commander.turnCounter > updateTurn || targetsChanged())) {
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


    public int[] stepOut(int[] pos) {
        if (valueAt(pos[0],pos[1]) < 0f) return null;
        int[] lastpos = new int[2];
        lastpos[0] = pos[0];
        lastpos[1] = pos[1];
        while (valueAt(pos[0],pos[1]) > 0f) {
            lastpos[0] = pos[0];
            lastpos[1] = pos[1];
            pos = stepDown(pos);
        }
        return lastpos;
    }

    void update() {
        for (int i=0;i<map.length;i++) {
            for (int j = 0;j < map[0].length;j++)
                map[i][j] = -1f;
        }
        edgeI = 0;
        targets.clear();
        updateTargets();
        for (int[] tp : targets) {
            map[tp[0]][tp[1]] = 0f;
            edges[edgeI][0] = tp[0]; edges[edgeI][1] = tp[1];
            edgeI++;
        }

        float step = 0f;
        while (edgeI > 0) {
            newEdgeI = 0;
            for (int ei = 0;ei< edgeI;ei++) {
                int[] edge = edges[ei];
                if (map[edge[0]][edge[1]] <= step) {
                    for (int[] dir : dirs) {
                        int nx = edge[0]+dir[0];
                        int ny = edge[1]+dir[1];
                        if (nx >= 0 && ny >= 0 && nx < map.length && ny < map[0].length) {
                            if (map[nx][ny] < 0f) {
                                UTerrain t = area.terrainAt(nx, ny);
                                if (t != null) {
                                    if (t.passable(moveTypes)) {
                                        map[nx][ny] = map[edge[0]][edge[1]] + 1f / t.getMovespeed();
                                        newEdges[newEdgeI][0] = nx;
                                        newEdges[newEdgeI][1] = ny;
                                        newEdgeI++;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    newEdges[newEdgeI][0] = edge[0];
                    newEdges[newEdgeI][1] = edge[1];
                    newEdgeI++;
                }
            }
            int[][] tmp = edges;
            edges = newEdges;
            newEdges = tmp;
            edgeI = newEdgeI;
            step = step + 1f;
        }
        updateTurn = commander.turnCounter;
        dirty = false;
    }
}
