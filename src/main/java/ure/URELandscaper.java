package ure;

import ure.terrain.URETerrain;
import ure.terrain.URETerrainCzar;
import ure.things.UREThing;
import ure.things.UREThingCzar;

import java.util.ArrayList;
import java.util.Random;

public class URELandscaper {

    private URETerrainCzar terrainCzar;
    private UREThingCzar thingCzar;

    public Random random;
    USimplexNoise simplexNoise;

    class Grid {
        boolean cells[][];
        int width;
        int height;
        public Grid(int w, int h) {
            cells = new boolean[w][h];
            width = w;
            height = h;
        }
        public void set(int x, int y, boolean val) {
            if (x>=0 && x<width && y>=0 && y<height)
                cells[x][y] = val;
        }
        public boolean get(int x, int y) {
            if (x>=0 && x<width && y>=0 && y<height)
                return cells[x][y];
            return true;
        }
        public void copyFrom(Grid src) {
            for (int x=0;x<width;x++) {
                for (int y=0;y<height;y++) {
                    cells[x][y] = src.get(x,y);
                }
            }
        }
        public int neighborsAt(int x, int y) { return neighborsAt(x, y, 1); }
        public int neighborsAt(int x, int y, int dist) {
            int neighbors = 0;
            for (int ox=-dist;ox<=dist;ox++) {
                for (int oy=-dist;oy<=dist;oy++) {
                    if (get(x+ox,y+oy))
                        neighbors++;
                }
            }
            return neighbors;
        }
        public int flood(int x, int y) {
            int total = 0;
            if (x<0 || x>=width || y<0 || y>= height)
                return 0;
            if (cells[x][y])
                return 0;
            cells[x][y] = true;
            total++;
            total += flood(x+1,y);
            total += flood(x-1,y);
            total += flood(x,y+1);
            total += flood(x,y-1);
            return total;
        }
    }

    public URELandscaper(URETerrainCzar theTerrainCzar, UREThingCzar theThingCzar) {
        terrainCzar = theTerrainCzar;
        thingCzar = theThingCzar;
        random = new Random();
        simplexNoise = new USimplexNoise();
    }

    public void buildArea(UREArea area) {
        System.out.println("Default landscaper cannot build areas!");
    }

    public void fillRect(UREArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, true); }
    public void drawRect(UREArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, false); }
    public void drawRect(UREArea area, String t, int x1, int y1, int x2, int y2, boolean filled) {
        for (int x=x1;x<=x2;x++) {
            for (int y=y1;y<=y2;y++) {
                if (filled || y == y1 || y == y2 || x == x1 | x == x2) {
                    area.setTerrain(x, y, t);
                }
            }
        }
    }

    public int rand(int max) {
        return random.nextInt(max);
    }
    public float randf(float max) {
        return random.nextFloat() * max;
    }
    public float randf() {
        return random.nextFloat();
    }

    public void scatterThings(UREArea area, String[] things, String[] terrains, int numberToScatter) {
        while (numberToScatter > 0) {
            numberToScatter--;
            UCell cell = randomCell(area, terrains);
            String name = things[random.nextInt(things.length)];
            UREThing thing = thingCzar.getThingByName(name);
            thing.moveToCell(area, cell.x, cell.y);
        }
    }

    public void spawnThingAt(UREArea area, int x, int y, String thing) {
        UREThing thingobj = thingCzar.getThingByName(thing);
        thingobj.moveToCell(area,x,y);
    }
    public void spawnLightAt(UREArea area, int x, int y, UColor color, int falloff, int range) {
        URELight light = new URELight(color, range, falloff);
        light.moveTo(area,x,y);
    }

    boolean cellHasTerrain(UREArea area, UCell cell, String[] terrains) {
        if (cell == null) return false;
        for (int i=0;i<terrains.length;i++) {
            if (terrains[i].equals(cell.terrain.name)) {
                return true;
            }
        }
        return false;
    }

    boolean[][] neighborsHaveTerrain(UREArea area, UCell cell, String[] terrains) {
        boolean[][] neighbors = new boolean[3][3];
        for (int xo=-1;xo<2;xo++) {
            for (int yo=-1;yo<2;yo++) {
                neighbors[xo+1][yo+1] = cellHasTerrain(area, area.cellAt(cell.x+xo,cell.y+yo), terrains);
            }
        }
        return neighbors;
    }
    int numNeighborsHaveTerrain(UREArea area, UCell cell, String[] terrains) {
        boolean[][] neighbors = neighborsHaveTerrain(area, cell, terrains);
        int total = 0;
        for (int xo=-1;xo<2;xo++) {
            for (int yo=-1;yo<2;yo++) {
                if (neighbors[xo+1][yo+1])
                    total++;
            }
        }
        return total;
    }

    public UCell randomCell(UREArea area, String[] terrains) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = area.cellAt(random.nextInt(area.xsize), random.nextInt(area.ysize));
            match = cellHasTerrain(area, cell, terrains);
        }
        return cell;
    }

    public void addDoors(UREArea area, String doorTerrain, String[] wallTerrains, float doorChance) {
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (area.cellAt(x,y).terrain.isPassable()) {
                    boolean[][] neighbors = neighborsHaveTerrain(area, area.cellAt(x,y), wallTerrains);
                    int neighborCount = numNeighborsHaveTerrain(area, area.cellAt(x,y), wallTerrains);
                    if ((neighbors[0][1] && neighbors[2][1] && !neighbors[1][0] && !neighbors[1][2]) ||
                        !neighbors[0][1] && !neighbors[2][1] && neighbors[1][0] && neighbors[1][2]) {
                        if (neighborCount <= 5) {
                            area.setTerrain(x,y,doorTerrain);
                        }
                    }
                }
            }
        }
    }

    public void simplexScatterTerrain(UREArea area, String terrain, String[] targets, float threshold, float scatterChance) {
        float[] noiseScales = new float[]{5f, 10f, 40f, 120f};
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (cellHasTerrain(area, area.cellAt(x,y), targets)) {
                    float sample = simplexNoise.multi(x, y, noiseScales);
                    if (sample > threshold) {
                        if (random.nextFloat() <= scatterChance) {
                            area.setTerrain(x, y, terrain);
                        }
                    }
                }
            }
        }
    }

    public void simplexScatterThings(UREArea area, String thing, String[] targets, float threshold, float scatterChance) {
        float[] noiseScales = new float[]{5f, 10f, 40f, 120f};
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (cellHasTerrain(area, area.cellAt(x,y), targets)) {
                    float sample = simplexNoise.multi(x, y, noiseScales);
                    if (sample > threshold) {
                        if (random.nextFloat() <= scatterChance) {
                            UREThing thingobj = thingCzar.getThingByName(thing);
                            thingobj.moveToCell(area,x,y);
                        }
                    }
                }
            }
        }
    }

    public void digRiver(UREArea area, String t, int x1, int y1, int x2, int y2, float riverWidth, float twist, float twistmax) {
        int width = x2-x1; int height = y2-y1;
        int edge = random.nextInt(4);
        float startx, starty, dx, dy, ctwist;
        if (edge == 0) {
            starty = (float)y1;  startx = (float)(random.nextInt(width) + x1);
            dx = 0f ; dy = 1f;
        } else if (edge == 1) {
            starty = (float)height; startx = (float)(random.nextInt(width) + x1);
            dx = 0f; dy = -1f;
        } else if (edge == 2) {
            startx = (float)x1; starty = (float)(random.nextInt(height) + y1);
            dx = 1f; dy = 0f;
        } else {
            startx = (float)width; starty = (float)(random.nextInt(height) + y1);
            dx = -1f; dy = 0f;
        }
        ctwist = 0f;
        boolean hitedge = false;
        while (!hitedge) {
            fillRect(area, t, (int)startx, (int)starty, (int)(startx+riverWidth), (int)(starty+riverWidth));
            startx += dx;
            starty += dy;
            if (random.nextFloat() < twist) {
                if (dx == 0) {
                    startx += ctwist;
                }
                if (dy == 0) {
                    starty += ctwist;
                }
            }
            if (startx > x2 || startx < x1 || starty > y2 || starty < y1) {
                hitedge = true;
            }
            ctwist = ctwist + random.nextFloat() * twist - (twist/2f);
            if (ctwist > twistmax) ctwist = twistmax;
            if (ctwist < -twistmax) ctwist = -twistmax;
        }
    }

    public void digCaves(UREArea area, String t, int x1, int y1, int x2, int y2) {
        digCaves(area,t,x1,y1,x2,y2,0.42f,6,4,3);
    }
    public void digCaves(UREArea area, String t, int x1, int y1, int x2, int y2, float initialDensity, int jumblePasses, int jumbleDensity, int smoothPasses) {
        int width = x2-x1; int height = y2-y1;
        Grid map = new Grid(width,height);
        Grid scratchmap = new Grid(width,height);
        float fillratio = 0f;
        while (fillratio < 0.2f) {
            int gapY = random.nextInt(height / 2) + height / 3;
            for (int x = 0;x < width;x++) {
                for (int y = 0;y < height;y++) {
                    if ((y < gapY || y > gapY + 1) && random.nextFloat() < initialDensity)
                        map.set(x, y, true);
                    else
                        map.set(x, y, false);
                }
            }
            for (int i = 0;i < jumblePasses;i++) {
                System.out.println("  jumble " + Integer.toString(i));
                for (int x = 0;x < width;x++) {
                    for (int y = 0;y < height;y++) {
                        if (map.neighborsAt(x, y) >= 5 || map.neighborsAt(x, y, 2) <= jumbleDensity) {
                            scratchmap.set(x, y, true);
                        } else {
                            scratchmap.set(x, y, false);
                        }
                    }
                }
                map.copyFrom(scratchmap);
            }
            for (int i = 0;i < smoothPasses;i++) {
                System.out.println("  smooth " + Integer.toString(i));
                for (int x = 0;x < width;x++) {
                    for (int y = 0;y < height;y++) {
                        if (map.neighborsAt(x, y) >= 5) {
                            scratchmap.set(x, y, true);
                        } else {
                            scratchmap.set(x, y, false);
                        }
                    }
                }
                map.copyFrom(scratchmap);
            }
            scratchmap.copyFrom(map);
            int x = width / 2;
            int y = height / 2;
            while (scratchmap.get(x, y)) {
                x = random.nextInt(width - 2) + 2;
                y = random.nextInt(height - 2) + 2;
            }
            int spacecount = scratchmap.flood(x, y);
            fillratio = (float) spacecount / (float) (width * height);
        }
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                if (!map.get(x,y) && scratchmap.get(x,y))
                    area.setTerrain(x+x1, y+y1, t);
            }
        }
    }

    boolean canFitBoxAt(UREArea area, int x, int y, int width, int height, String[] floorTerrains) {
        for (int xo=0;xo<width;xo++) {
            for (int yo=0;yo<height;yo++) {
                if (!cellHasTerrain(area, area.cellAt(x+xo,y+yo), floorTerrains))
                    return false;
            }
        }
        return true;
    }

    int[] locateBox(UREArea area, int width, int height, String[] floorTerrains) {
        int tries = 200;
        while (tries > 0) {
            tries--;
            int x = random.nextInt(area.xsize);
            int y = random.nextInt(area.ysize);
            if (canFitBoxAt(area, x, y, width, height, floorTerrains)) {
                System.out.println("found a box");
                int[] coords = new int[2];
                coords[0] = x;
                coords[1] = y;
                return coords;
            }
        }
        return null;
    }
}