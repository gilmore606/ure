package ure.areas;

import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.ui.ULight;
import ure.math.UColor;
import ure.math.USimplexNoise;
import ure.terrain.Stairs;
import ure.terrain.UTerrain;
import ure.terrain.UTerrainCzar;
import ure.things.UThing;
import ure.things.UThingCzar;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Random;

/**
 * ULandscaper is a grab bag of tools for creating and populating UAreas.
 *
 * To use, make a subclass and implement buildArea() to construct an area using the provided
 * methods.  Then use an instance of your subclass in your UCartographer implementation.
 *
 */
public class ULandscaper {

    @Inject
    public UCommander commander;
    @Inject
    UActorCzar actorCzar;
    @Inject
    UTerrainCzar terrainCzar;
    @Inject
    UThingCzar thingCzar;

    public Random random;
    USimplexNoise simplexNoise;

    public String floorterrain = "rock";

    /**
     * Grid implements a 2D boolean grid useful for proxy calculations about terrain.
     *
     */
    class Grid {
        boolean cells[][];
        int width;
        int height;
        public Grid(int w, int h) {
            cells = new boolean[w][h];
            width = w;
            height = h;
        }
        public void set(int[] c, boolean val) {
            set(c[0],c[1],val);
        }
        public void set(int x, int y, boolean val) {
            if (x>=0 && x<width && y>=0 && y<height)
                cells[x][y] = val;
        }
        public boolean get(int[] c) {
            return get(c[0],c[1]);
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
        public int flood_deprecated(int x, int y) {
            // recursive, blows out the stack
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
            System.out.println("GEN : flood_deprecated found " + Integer.toString(total) + " cells");
            return total;
        }
        public int flood(int x, int y) {
            ArrayList<int[]> q = new ArrayList<int[]>();
            if (cells[x][y]) return 0;
            int total = 0;
            q.add(new int[]{x,y});
            int[] n = new int[]{0,0};
            ArrayList<int[]> noobs = new ArrayList<int[]>();
            while (!q.isEmpty()) {
                noobs.clear();
                for (int[] N : q) {
                    int[] w = new int[]{N[0],N[1]};
                    int[] e = new int[]{N[0],N[1]};
                    while (!get(w[0] - 1, w[1]))
                        w[0] = w[0] - 1;
                    while (!get(e[0] + 1, e[1]))
                        e[0] = e[0] + 1;
                    for (int i = w[0];i <= e[0];i++) {
                        n[0] = i;
                        n[1] = w[1];
                        set(n, true); total++;
                        if (!get(n[0], n[1] - 1)) noobs.add(new int[]{n[0], n[1] - 1});
                        if (!get(n[0], n[1] + 1)) noobs.add(new int[]{n[0], n[1] + 1});
                    }
                }
                q.clear();
                for (int[] noob : noobs) {
                    q.add(noob);
                }
            }
            System.out.println("GEN : flood found " + Integer.toString(total) + " cells");
            return total;
        }
    }

    public ULandscaper() {
        Injector.getAppComponent().inject(this);
        random = new Random();
        simplexNoise = new USimplexNoise();
    }

    /**
     * Construct a complete area, given a blank UArea.
     *
     * Override this and add parameters to be passed in by your UCartographer to determine the
     * character of the area you produce.
     *
     * You <b>must</b> call SetStairsLabels() at the end of this method before you return the area to ensure that
     * area links are properly created.
     *
     * @param area
     */
    public void buildArea(UArea area) {
        System.out.println("Default landscaper cannot build areas!");
    }

    /**
     * Fill a rectangle from x1,y1 to x2,y2 with the given terrain.
     *
     * @param area
     * @param t
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void fillRect(UArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, true); }
    public void drawRect(UArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, false); }
    public void drawRect(UArea area, String t, int x1, int y1, int x2, int y2, boolean filled) {
        for (int x=x1;x<=x2;x++) {
            for (int y=y1;y<=y2;y++) {
                if (filled || y == y1 || y == y2 || x == x1 | x == x2) {
                    area.setTerrain(x, y, t);
                }
            }
        }
    }

    /**
     * Convenience random numbers.
     * @param max 0-max exclusive.
     * @return
     */
    public int rand(int max) {
        return random.nextInt(max);
    }
    public float randf(float max) {
        return random.nextFloat() * max;
    }
    public float randf() {
        return random.nextFloat();
    }

    /**
     * Scatter a number of random things through the area, onto certain terrains.
     *
     * @param area
     * @param things Thing names to scatter.  List names more than once for more frequency.
     * @param terrains Terrain names to receive things.
     * @param numberToScatter Total number of things to scatter.
     */
    public void scatterThings(UArea area, String[] things, String[] terrains, int numberToScatter) {
        while (numberToScatter > 0) {
            numberToScatter--;
            UCell cell = randomCell(area, terrains);
            String name = things[random.nextInt(things.length)];
            UThing thing = thingCzar.getThingByName(name);
            thing.moveToCell(area, cell.x, cell.y);
        }
    }

    /**
     * Spawn a new thing by name into the area.
     *
     * @param area
     * @param x
     * @param y
     * @param thing
     */
    public void spawnThingAt(UArea area, int x, int y, String thing) {
        UThing thingobj = thingCzar.getThingByName(thing);
        thingobj.moveToCell(area,x,y);
    }

    /**
     * Spawn an abstract light directly into the area.
     *
     * @param area
     * @param x
     * @param y
     * @param color
     * @param falloff
     * @param range
     */
    public void spawnLightAt(UArea area, int x, int y, UColor color, int falloff, int range) {
        ULight light = new ULight(color, range, falloff);
        light.moveTo(area,x,y);
    }

    /**
     * Does cell have any of these terrain names?
     *
     * @param area
     * @param cell
     * @param terrains
     * @return
     */
    boolean cellHasTerrain(UArea area, UCell cell, String[] terrains) {
        if (cell == null) return false;
        for (int i=0;i<terrains.length;i++) {
            if (terrains[i].equals(cell.getTerrain().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Which of cell's neighbors (and cell) have these terrain names?
     *
     * @param area
     * @param cell
     * @param terrains
     * @return A 2D grid of cell's immediate surroundings, true if that cell has one
     * of the given terrain names.
     */
    boolean[][] neighborsHaveTerrain(UArea area, UCell cell, String[] terrains) {
        boolean[][] neighbors = new boolean[3][3];
        for (int xo=-1;xo<2;xo++) {
            for (int yo=-1;yo<2;yo++) {
                neighbors[xo+1][yo+1] = cellHasTerrain(area, area.cellAt(cell.x+xo,cell.y+yo), terrains);
            }
        }
        return neighbors;
    }
    /**
     * How many of cell's neighbors (and cell) have these terrain names?
     *
     * @param area
     * @param cell
     * @param terrains
     * @return
     */
    int numNeighborsHaveTerrain(UArea area, UCell cell, String[] terrains) {
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

    /**
     * Pick a random cell having one of these terrains.
     *
     * @param area
     * @param terrains
     * @return
     */
    public UCell randomCell(UArea area, String[] terrains) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = area.cellAt(random.nextInt(area.xsize), random.nextInt(area.ysize));
            match = cellHasTerrain(area, cell, terrains);
        }
        return cell;
    }

    public UCell randomCell(UArea area, String terrain, int x1, int y1, int x2, int y2) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = area.cellAt(x1+rand(x2-x1), y1+rand(y2-y1));
            if (cell.terrain().getName().equals(terrain))
                return cell;
        }
        return null;
    }

    /**
     * Pick a random cell that can accept this thing.
     *
     * @param area
     * @param thing
     * @return
     */
    public UCell randomOpenCell(UArea area, UThing thing) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = area.cellAt(random.nextInt(area.xsize), random.nextInt(area.ysize));
            match = cell.willAcceptThing(thing);
        }
        return cell;
    }

    /**
     * Pick a random spawn location for this thing.
     */
    public UCell getRandomSpawn(UArea area, UThing thing, int x1, int y1, int x2, int y2) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = area.cellAt(x1+random.nextInt(x2-x1),y1+random.nextInt(y2-y1));
            if (cell != null) {
                match = cell.willAcceptThing(thing);
                if (match && !cell.terrain().isSpawnok())
                    match = false;
                if (match && !thing.canSpawnOnTerrain(cell.terrain().getName()))
                    match = false;
            }
        }
        return cell;
    }

    public UCell findAnextToB(UArea area, String ta, String tb, int x1, int y1, int x2, int y2) {
        int tries = 0;
        while (tries < ((x2-x1)*(y2-y1))*4) {
            tries++;
            UCell cell = area.cellAt(x1+rand(x2-x1),y1+rand(y2-y1));
            if (cell != null) {
                if (terrainNameAt(area, cell.x, cell.y).equals(ta)) {
                    if (terrainNameAt(area, cell.x + 1, cell.y).equals(tb)) return cell;
                    if (terrainNameAt(area, cell.x - 1, cell.y).equals(tb)) return cell;
                    if (terrainNameAt(area, cell.x, cell.y + 1).equals(tb)) return cell;
                    if (terrainNameAt(area, cell.x, cell.y - 1).equals(tb)) return cell;
                }
            }
        }
        return null;
    }

    public UCell findAreaWithout(UArea area, int x1, int y1, int x2, int y2, int w, int h, String[] terrains) {
        int tries = 0;
        while (tries < 500) {
            tries++;
            int x = rand(x2-x1)+x1;
            int y = rand(y2-y1)+y1;
            if (!rectHasTerrain(area, x, y, x+w, y+h, terrains)) {
                System.out.println("found area without at " + Integer.toString(x) + "," + Integer.toString(y));
                return area.cellAt(x,y);
            }
        }
        return null;
    }

    public boolean isNearA(UArea area, int x, int y, String thing, int range) {
        if (range < 1)
            return false;
        for (int ix = x - range; ix < x + range; ix++) {
            for (int iy = y - range; iy < y + range; iy++) {
                if (area.isValidXY(ix,iy)) {
                    if (area.cellAt(ix,iy).hasA(thing)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addDoors(UArea area, String doorTerrain, String[] wallTerrains, float doorChance) {
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (area.cellAt(x, y).getTerrain().isPassable()) {
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

    public void simplexScatterTerrain(UArea area, String terrain, String[] targets, float threshold, float scatterChance, float[] noiseScales) {
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

    public void simplexScatterThings(UArea area, String thing, String[] targets, float threshold, float scatterChance, float[] noiseScales, int separateBy) {
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (cellHasTerrain(area, area.cellAt(x,y), targets)) {
                    float sample = simplexNoise.multi(x, y, noiseScales);
                    if (sample > threshold) {
                        if (random.nextFloat() <= scatterChance) {
                            if (!isNearA(area,x,y,thing,separateBy)) {
                                UThing thingobj = thingCzar.getThingByName(thing);
                                thingobj.moveToCell(area, x, y);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean rectHasTerrain(UArea area, int x1, int y1, int x2, int y2, String[] terrains) {
        for (;x1<=x2;x1++) {
            for (;y1<=y2;y1++) {
                if (area.isValidXY(x1,y1)) {
                    String ts = area.terrainAt(x1,y1).getName();
                    for (String s : terrains) {
                        if (s.equals(ts))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public String terrainNameAt(UArea area, int x, int y) {
        UCell cell = area.cellAt(x,y);
        if (cell != null) {
            return cell.terrain().getName();
        }
        return "";
    }

    public void digRiver(UArea area, String t, int x1, int y1, int x2, int y2, float riverWidth, float twist, float twistmax) {
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

    public void digCaves(UArea area, String t, int x1, int y1, int x2, int y2) {
        digCaves(area,t,x1,y1,x2,y2,0.42f,6,4,3);
    }
    public void digCaves(UArea area, String t, int x1, int y1, int x2, int y2, float initialDensity, int jumblePasses, int jumbleDensity, int smoothPasses) {
        int width = x2-x1; int height = y2-y1;
        Grid map = new Grid(width,height);
        Grid scratchmap = new Grid(width,height);
        float fillratio = 0f;
        int tries = 0;
        while ((fillratio < 0.25f) && (tries < 8)) {
            tries++;
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
            int rantries = 0;
            while (scratchmap.get(x, y) && rantries < 500) {
                rantries++;
                x = random.nextInt(width - 2) + 2;
                y = random.nextInt(height - 2) + 2;
            }
            int spacecount = scratchmap.flood(x, y);
            fillratio = (float) spacecount / (float) (width * height);
        }
        System.out.println("printing cave dig into area");
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                if (!map.get(x,y) && scratchmap.get(x,y))
                    area.setTerrain(x+x1, y+y1, t);
            }
        }
    }

    boolean canFitBoxAt(UArea area, int x, int y, int width, int height, String[] floorTerrains) {
        for (int xo=0;xo<width;xo++) {
            for (int yo=0;yo<height;yo++) {
                if (!cellHasTerrain(area, area.cellAt(x+xo,y+yo), floorTerrains))
                    return false;
            }
        }
        return true;
    }

    public int[] locateBox(UArea area, int width, int height, String[] floorTerrains) {
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

    public void placeStairs(UArea area, String exitType, String label) {
        UCell cell = randomOpenCell(area, commander.player());
        area.setTerrain(cell.x, cell.y, exitType);
        UTerrain t = area.terrainAt(cell.x,cell.y);
        if (t instanceof Stairs)
            ((Stairs)t).setLabel(label);
    }

    /**
     * Set the label for a Stairs (in the given area) to its proper outgoing area.
     *
     * This should be called by any ULandscaper.makeArea() before returning.
     *
     * @param area
     * @param x
     * @param y
     * @param t
     */
    public void SetStairsLabel(UArea area, UCartographer carto, int x, int y, Stairs t) {
        t.setLabel("", carto);
    }

    public void buildRoom(UArea area, int x, int y, int w, int h, String floort, String wallt) {
        fillRect(area, floort, x, y, (x+w)-1, (y+h)-1);
        drawRect(area, wallt, x, y, (x+w)-1, (y+h)-1);
    }

    public void buildComplex(UArea area, int x1, int y1, int x2, int y2, String floort, String wallt, String[] drawoverts, int roomsizeMin, int roomsizeMax, float hallChance, int hallwidth, int roomsmax, int minroomarea) {
        ArrayList<int[]> rooms = new ArrayList<int[]>();
        boolean addExteriorDoors = true;
        boolean addExteriorWindows = true;
        int firstw = roomsizeMin + rand(roomsizeMax-roomsizeMin);
        int firsth = roomsizeMin + rand(roomsizeMax-roomsizeMin);
        int firstx = x1 + (x2-x1)/2;
        int firsty = y1 + (y2-y1)/2;
        buildRoom(area, firstx,firsty,firstw,firsth, floort, wallt);
        rooms.add(new int[]{firstx,firsty,firstw,firsth});
        boolean done = false;
        int fails = 0;
        while (!done && (fails < rooms.size()*6) && (rooms.size() < roomsmax)) {
            int[] sourceroom = rooms.get(rand(rooms.size()));
            int wallid = rand(4);
            int dx, dy, sx, sy;
            if (wallid == 0) {
                dx = 0; dy = -1;
                sx = sourceroom[0]; sy = sourceroom[1];
            } else if (wallid == 1) {
                dx = 1; dy = 0;
                sx = sourceroom[0] + sourceroom[2] - 1; sy = sourceroom[1];
            } else if (wallid == 2) {
                dx = 0; dy = 1;
                sx = sourceroom[0]; sy = sourceroom[1] + sourceroom[3] - 1;
            } else {
                dx = -1; dy = 0;
                sx = sourceroom[0]; sy = sourceroom[1];
            }
            int[] newbox = new int[]{0,0};
            if (randf() < hallChance) {
                newbox[0] = hallwidth;
                newbox[1] = roomsizeMin + rand(roomsizeMax-roomsizeMin);
            } else {
                while (newbox[0] * newbox[1] < minroomarea) {
                    newbox[0] = roomsizeMin + rand(roomsizeMax - roomsizeMin);
                    newbox[1] = roomsizeMax + rand(roomsizeMax - roomsizeMin);
                }
            }
            if ((sourceroom[2] > sourceroom[3] && newbox[0] > newbox[1]) || (sourceroom[2] < sourceroom[3] && newbox[0] < newbox[1])) {
                int tmp = newbox[0];
                newbox[0] = newbox[1];
                newbox[1] = tmp;
            }
            int slidemin, slidemax;
            if (dx == 0) {
                slidemin = (0 - newbox[0]) + 3;
                slidemax = (sourceroom[2]) - 3;
            } else {
                slidemin = (0 - newbox[1]) + 3;
                slidemax = (sourceroom[3]) - 3;
            }
            int[][] connectpoints = new int[(slidemax-slidemin)+2][2];  int connecti = 0;
            int[] slidepos = new int[2];
            int[] newroom = new int[4];
            for (int slide=slidemin;slide<slidemax;slide++) {
                slidepos[0] = (sx + slide * Math.abs(dy));
                slidepos[1] = (sy + slide * Math.abs(dx));
                int newroomtest[] = new int[]{slidepos[0]+dx,slidepos[1]+dy,newbox[0]-Math.abs(dx),newbox[1]-Math.abs(dy)};
                newroom = new int[]{slidepos[0],slidepos[1],newbox[0],newbox[1]};
                if (dx<0) {
                    newroomtest[0] = 1 + slidepos[0] - newbox[0];
                    newroom[0] = 1 + slidepos[0] - newbox[0];
                }
                if (dy<0) {
                    newroomtest[1] = 1 + slidepos[1] - newbox[1];
                    newroom[1] = 1 + slidepos[1] - newbox[1];
                }
                if (canFitBoxAt(area, newroomtest[0],newroomtest[1],newroomtest[2],newroomtest[3], drawoverts)) {
                    if (newroomtest[0] >= x1 && newroomtest[1] >= y1 && newroomtest[0]+newroomtest[2] <= x2 && newroomtest[1]+newroomtest[3] <= y2) {
                        connectpoints[connecti] = new int[]{slidepos[0], slidepos[1]};
                        connecti++;
                    }
                }
            }
            if (connecti > 0) {
                int[] newloc = connectpoints[rand(connecti)];
                newroom[0] = newloc[0];
                newroom[1] = newloc[1];
                newroom[2] = newbox[0];
                newroom[3] = newbox[1];
                if (dy == -1) {
                    newroom[1] = newroom[1] - (newbox[1] - 1);
                }
                if (dx == -1) {
                    newroom[0] = newroom[0] - (newbox[0] - 1);
                }
                int doormin = 0, doormax = 0, doorconst = 0;
                if (dy != 0) {
                    doormin = Math.max(sourceroom[0]+1,newroom[0]+1);
                    doormax = Math.min(sourceroom[0]+sourceroom[2]-2,newroom[0]+newroom[2]-2);
                    if (dy == -1) doorconst = sourceroom[1]; else doorconst = sourceroom[1] + sourceroom[3] - 1;
                } else if (dx != 0) {
                    doormin = Math.max(sourceroom[1]+1,newroom[1]+1);
                    doormax = Math.min(sourceroom[1]+sourceroom[3]-2,newroom[1]+newroom[3]-2);
                    if (dx == -1) doorconst = sourceroom[0]; else doorconst = sourceroom[0] + sourceroom[2] - 1;
                }
                buildRoom(area, newroom[0],newroom[1],newroom[2],newroom[3],floort,wallt);
                rooms.add(newroom);
                int doorstyle = rand(2);
                if (doorstyle == 0) {
                    int mid = doormin;
                    if (doormax > doormin) mid = rand(doormax-doormin)+doormin;
                    if (dy != 0) area.setTerrain(mid, doorconst, "door");
                    else area.setTerrain(doorconst,mid,"door");
                } else if (doorstyle == 1) {
                    for (int i = doormin;i <= doormax;i++) {
                        if (dy != 0) {
                            area.setTerrain(i, doorconst, floort);
                        } else {
                            area.setTerrain(doorconst, i, floort);
                        }
                    }
                }
                System.out.println("CARTO : made new room " + Integer.toString(newroom[2]) + " by " + Integer.toString(newroom[3]));
                fails = 0;
            } else {
                fails++;
                System.out.println("CARTO : couldn't add room");
            }
        }
        for (int[] room : rooms) {
            DecorateRoom(area, room);
        }
        if (addExteriorDoors && rooms.size() > 1) {
            for (int i = 0;i < rand(rooms.size()/2) + 2;i++) {
                UCell doorcell = findAnextToB(area, wallt, "grass", x1 + 1, y1 + 1, x2 - 1, y2 - 1);
                if (doorcell != null) {
                    area.setTerrain(doorcell.x, doorcell.y, "door");
                }
            }
        }
        if (addExteriorWindows && rooms.size() > 1) {
            for (int i=0;i<rand(20)+10;i++) {
                UCell windowcell = findAnextToB(area, wallt, "grass", x1+1,y1+1,x2-1,y2-1);
                if (windowcell != null) {
                    area.setTerrain(windowcell.x, windowcell.y, "window");
                }
            }
        }
    }

    void DecorateRoom(UArea area, int[] room) {
        int x1 = room[0];
        int y1 = room[1];
        int w = room[2];
        int h = room[3];
        if (randf() < 0.2f) {
            fillRect(area, "carvings", x1+1,y1+1,x1+w-3,y1+h-3);
        }
    }


    public void scatterActorsByTags(UArea area, int x1, int x2, int y1, int y2, String[] tags, int level, int amount) {
        ArrayList<String> names = new ArrayList<>();
        for (String tag : tags) {
            System.out.println("get names for " + tag);
            String[] thenames = actorCzar.getActorsByTag(tag,level);
            for (String name: thenames) {
                names.add(name);
            }
        }
        while (amount > 0) {
            amount--;
            String name;
            if (names.size() == 1)
                name = names.get(0);
            else
                name = names.get(random.nextInt(names.size()));
            UActor actor = actorCzar.getActorByName(name);
            UCell dest = getRandomSpawn(area, actor, x1, x2, y1, y2);
            actor.moveToCell(area, dest.x, dest.y);
        }
    }

    public void scatterThingsByTags(UArea area, int x1, int x2, int y1, int y2, String[] tags, int level, int amount) {
        ArrayList<String> names = new ArrayList<>();
        for (String tag : tags) {
            System.out.println("get names for " + tag);
            String[] thenames = thingCzar.getThingsByTag(tag,level);
            for (String name: thenames) {
                names.add(name);
            }
        }
        while (amount > 0) {
            amount--;
            String name;
            if (names.size() == 1)
                name = names.get(0);
            else
                name = names.get(random.nextInt(names.size()));
            UThing thing = thingCzar.getThingByName(name);
            UCell dest = getRandomSpawn(area, thing, x1, x2, y1, y2);
            thing.moveToCell(area, dest.x, dest.y);
        }
    }

    public void linkRegionAt(UArea area, int x, int y, String exittype, URegion region, int destlevel, String backexittype) {
        region.addLink(destlevel, backexittype, area.label);
        commander.cartographer.addRegion(region);
        area.setTerrain(x,y,exittype);
        ((Stairs)area.terrainAt(x,y)).setLabel(region.id + " " + Integer.toString(destlevel));
    }
}