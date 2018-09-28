package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;
import ure.math.DimapShape;

import java.util.ArrayList;

public class Connector extends Shaper {

    public class Tunnel {
        int startx,starty;
        int[][] points;
        int length;
        int dx,dy;
        boolean goneLeft, goneRight;
        Shape startShape;
        Shape hitShape;

        public Tunnel(Shape startShape, int startx, int starty, ArrayList<Shape> targets, Shape mask, float turnChance) {
            this.startx = startx;
            this.starty = starty;
            this.startShape = startShape;
            this.hitShape = null;
            boolean founddir = false;
            while (!founddir) {
                int dir = random.i(4);
                if (dir == 0) {
                    if (!startShape.value(startx-1,starty)) {
                        dx = -1; dy = 0; founddir = true;
                    }
                } else if (dir == 1) {
                    if (!startShape.value(startx+1,starty)) {
                        dx = 1; dy = 0; founddir = true;
                    }
                } else if (dir == 2) {
                    if (!startShape.value(startx,starty-1)) {
                        dx = 0; dy = -1; founddir = true;
                    }
                } else if (dir == 3) {
                    if (!startShape.value(startx, starty+1)) {
                        dx = 0; dy = 1; founddir = true;
                    }
                }
            }
            points = new int[(mask.xsize+mask.ysize)*2][2];
            boolean hit = false;
            int cx = startx; int cy = starty;
            goneLeft = false;
            goneRight = false;
            while (!hit) {
                cx += dx;
                cy += dy;
                if (!mask.isValidXY(cx,cy)) {
                    break;
                } else if (mask.value(cx,cy)) {
                    hit = true;
                } else if ((length > 2) && (mask.value(cx-1,cy) || mask.value(cx+1,cy) || mask.value(cx,cy+1) || mask.value(cx,cy-1))) {
                    hit = true;
                }
                if (hit) {
                    for (Shape test : targets) {
                        if (test.value(cx,cy) || test.value(cx-1,cy) || test.value(cx+1,cy) || test.value(cx,cy+1) || test.value(cx,cy-1)) {
                            hitShape = test;
                        }
                    }
                }
                points[length][0] = cx;
                points[length][1] = cy;
                length++;
                if (!hit && (length > 2) && (random.f() < turnChance)) {
                    int turn = 0;
                    if (goneLeft) {
                        turn = 1;
                        goneLeft = false;
                    } else if (goneRight) {
                        turn = -1;
                        goneRight = false;
                    } else {
                        turn = random.f() < 0.5f ? -1 : 1;
                        if (turn == 1)
                            goneRight = true;
                        else
                            goneLeft = true;
                    }
                    if (turn == 1) {
                        if (dy == 1) {
                            dy = 0; dx = -1;
                        } else if (dy == -1) {
                            dy = 0; dx = 1;
                        } else if (dx == 1) {
                            dx = 0; dy = 1;
                        } else {
                            dx = 0; dy = -1;
                        }
                    } else if (turn == -1) {
                        if (dy == 1) {
                            dy = 0; dx = 1;
                        } else if (dy == -1) {
                            dy = 0; dx = -1;
                        } else if (dx == 1) {
                            dx = 0; dy = -1;
                        } else {
                            dx = 0; dy = 1;
                        }
                    }
                }
            }
        }

        public void print(Shape shape) {
            int i = 0;
            while (i < length) {
                shape.set(points[i][0],points[i][1]);
                i++;
            }
        }
    }

    public Connector(int xsize, int ysize) {
        super(xsize,ysize);
        name = "Connector";
    }

    @Override
    public void setupParams() {
        addParamB("useTerrainSource", false);
        addParamT("terrainSource", "null");
        addParamF("turnChance", 0f, 0.2f, 0.6f);
        addParamI("optimality", 2, 20, 50);
        addParamF("minimizeConnections", 0.2f, 0.8f, 1f);
        addParamI("connectBatch", 1, 2, 5);
        addParamB("copySource", true);
        addParamI("smallestRegion", 1, 10, 200);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildConnector(previousLayer, area, getParamB("useTerrainSource"), getParamT("terrainSource"), getParamF("turnChance"), getParamI("optimality"),getParamF("minimizeConnections"),getParamI("connectBatch"),getParamI("smallestRegion"));
    }

    public void buildConnector(Layer previousLayer, UArea area, boolean useTerrainSource, String terrainSource, float turnChance, int optimality, float minimizeConnections, int connectBatch, int smallestRegion) {
        clear();
        if (previousLayer == null || area == null) return;
        Shape source = useTerrainSource ? makeTerrainMask(area,terrainSource) : previousLayer.shaper;
        ArrayList<Shape> regions = new ArrayList<>();
        ArrayList<Shape> allregions = new ArrayList<>();
        for (Shape r : source.regions()) {
            if (r.count() > smallestRegion) {
                regions.add(r);
                allregions.add(r);
                maskWith(r, MASK_XOR);
            }
        }
        int passes = 0;
        while (regions.size() > 1 && passes < 50) {
            passes++;
            Shape start = regions.get(random.i(regions.size()));
            System.out.println("connecting 1 of " + regions.size() + " regions");
            ArrayList<int[]> edges = start.edgeList();
            ArrayList<Tunnel> tunnels = new ArrayList<>();
            for (int i=0;i<optimality;i++) {
                int[] edge = edges.get(random.i(edges.size()));
                Tunnel tunnel = new Tunnel(start, edge[0], edge[1], allregions, this, turnChance);
                if (tunnel.hitShape != null && tunnel.hitShape != start) {
                    tunnels.add(tunnel);
                }
            }
            int kept = 0;
            while (tunnels.size() > 0 && kept < connectBatch) {
                int minI = 0; int minsize = 1000;
                for (int i=0;i<tunnels.size();i++) {
                    if (tunnels.get(i).length < minsize) {
                        minI = i;
                        minsize = tunnels.get(i).length;
                    }
                }
                Tunnel keeper = tunnels.get(minI);
                tunnels.remove(minI);
                kept++;
                keeper.print(this);
                keeper.print(source);
                keeper.print(start);
            }
            regions = regions();
        }
    }
}
