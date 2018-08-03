package ure.areas;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public abstract class ULandscaper {

    @Inject
    @JsonIgnore
    public UCommander commander;

    @Inject
    @JsonIgnore
    UActorCzar actorCzar;

    @Inject
    @JsonIgnore
    UTerrainCzar terrainCzar;

    @Inject
    @JsonIgnore
    UThingCzar thingCzar;

    @JsonIgnore
    protected Random random = new Random();

    @JsonIgnore
    USimplexNoise simplexNoise = new USimplexNoise();

    protected String floorterrain = "rock";

    /**
     * This field will be use to match the proper ULandscaper subclass when deserializing.  It will need
     * to match the static TYPE field in the subclass.
     */
    protected String type = "";



    /**
     * This constructor is necessary for deserialization, but generally you'll want your subclass
     * to call the one that allows you to pass in the type string below.
     */
    public ULandscaper() {
        Injector.getAppComponent().inject(this);
    }

    /**
     * Use this constructor to set the type field for this landscaper.  Usually you would invoke this
     * from a subclass' constructor and pass in the static TYPE field from your subclass so that we can
     * properly match up the classes when deserializing them.
     * @param type The type string that matches the subclass' TYPE field.
     */
    public ULandscaper(String type) {
        this();
        this.type = type;
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
    public void buildArea(UArea area, int level, String[] tags) {
        System.out.println("Default landscaper cannot build areas!");
    }

    /**
     * Fill a rectangle from x1,y1 to x2,y2 with the given terrain.
     *
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
            if (thing.getContents().getThings() == null)
                System.out.println("*** BUG scatterThings got a thing back with no contents.things bound for area " + area.label);
            thing.moveToCell(area, cell.x, cell.y);
        }
    }

    /**
     * Spawn a new thing by name into the area.
     *
     */
    public void spawnThingAt(UArea area, int x, int y, String thing) {
        UThing thingobj = thingCzar.getThingByName(thing);
        thingobj.moveToCell(area,x,y);
    }

    /**
     * Spawn an abstract light directly into the area.
     *
     */
    public void spawnLightAt(UArea area, int x, int y, UColor color, int falloff, int range) {
        ULight light = new ULight(color, range, falloff);
        light.moveTo(area,x,y);
    }

    /**
     * Does cell have any of these terrain names?
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

    public Shapemask shapeCaves(int xsize, int ysize) { return shapeCaves(xsize,ysize,0.45f,5,2,3); }
    public Shapemask shapeCaves(int xsize, int ysize, float initialDensity, int jumblePasses, int jumbleDensity, int smoothPasses) {
        Shapemask mask = new Shapemask(xsize, ysize);
        float fillratio = -1f;
        int tries = 0;
        while ((fillratio < 0.25f) && (tries < 8)) {
            System.out.println("SCAPER: shapeCaves attempt " + Integer.toString(tries));
            tries++;

            // Fill with initial noise, minus a horizontal gap (to promote connectedness later)
            mask.noiseWipe(initialDensity);
            int gapY = rand(ysize/2) + ysize/3;
            for (int x=0;x<xsize;x++) { mask.clear(x,gapY); mask.clear(x,gapY+1); mask.clear(x,gapY-1); }

            mask.jumble(5, jumbleDensity, jumblePasses);
            mask.smooth(5, smoothPasses);

            // Check if we made enough space
            int[] point = mask.randomCell(false);
            int spacecount = mask.floodCount(point[0],point[1],false);
            fillratio = (float)spacecount / (float)(xsize*ysize);
        }
        mask.invert();
        return mask;
    }

    public Shapemask shapeOval(int xsize, int ysize) {
        Shapemask mask = new Shapemask(xsize, ysize);
        int ox = xsize/2; int oy = ysize/2;
        int width = ox; int height = oy;
        int hh = height * height;
        int ww = width * width;
        int hhww = hh * ww;
        int x0 = width;
        int dx = 0;
        for (int x=-width;x<=width;x++)
            mask.set(ox+x,oy);
        for (int y=1;y<=height;y++) {
            int x1=x0-(dx-1);
            for ( ;x1>0;x1--) {
                if (x1 * x1 * hh + y * y * ww <= hhww)
                    break;
            }
            dx = x0-x1;
            x0 = x1;
            for (int x=-x0;x<=x0;x++) {
                mask.set(ox+x,oy-y);
                mask.set(ox+x,oy+y);
            }
        }
        return mask;
    }

    public Shapemask shapeBlob(int xsize, int ysize) {
        Shapemask mask = shapeOval(xsize, ysize);
        mask.erode(0.5f, 3);
        return mask;
    }

    public Shapemask shapeOddBlob(int xsize, int ysize) { return shapeOddBlob(xsize,ysize,3,0.3f); }
    public Shapemask shapeOddBlob(int xsize, int ysize, int parts, float twist) {
        Shapemask mask = new Shapemask(xsize,ysize);
        for (int i=0;i<parts;i++) {
            float xprop = 0.3f + commander.random.nextFloat()*0.7f - twist;
            float yprop = 0.3f + commander.random.nextFloat()*0.7f - twist;
            if (commander.random.nextFloat() > 0.5f) {
                float tmp = xprop;
                xprop = yprop;
                yprop = tmp;
            }
            Shapemask oval = shapeOval((int)(xsize * xprop), (int)(ysize * yprop));
            int xvar = (int)(xsize*(1f-xprop))+1;
            int yvar = (int)(ysize*(1f-yprop))+1;
            int xoff = commander.random.nextInt(xvar) - (xvar/2);
            int yoff = commander.random.nextInt(yvar) - (yvar/2);
            mask.maskWith(oval, Shapemask.MASKTYPE_OR, xoff, yoff);
        }
        mask.smooth(5, 2);
        mask.erode(0.5f, 2);
        return mask;
    }

    // TODO: variable width
    public Shapemask shapeRoad(int xsize, int ysize, float width, float twist, float twistmax) {
        Shapemask mask = new Shapemask(xsize, ysize);
        int edge = random.nextInt(4);
        float startx, starty, dx, dy, ctwist;
        if (edge == 0) {
            starty = 0f;  startx = (float)(random.nextInt(xsize));
            dx = 0f ; dy = 1f;
        } else if (edge == 1) {
            starty = (float)ysize; startx = (float)(random.nextInt(xsize));
            dx = 0f; dy = -1f;
        } else if (edge == 2) {
            startx = 0f; starty = (float)(random.nextInt(ysize));
            dx = 1f; dy = 0f;
        } else {
            startx = (float)xsize; starty = (float)(random.nextInt(ysize));
            dx = -1f; dy = 0f;
        }
        ctwist = 0f;
        boolean hitedge = false;
        while (!hitedge) {
            mask.fillRect((int)startx, (int)starty, (int)(startx+width), (int)(starty+width));
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
            if (startx >= xsize || startx < 0 || starty >= ysize || starty < 0) {
                hitedge = true;
            }
            ctwist = ctwist + random.nextFloat() * twist - (twist/2f);
            if (ctwist > twistmax) ctwist = twistmax;
            if (ctwist < -twistmax) ctwist = -twistmax;
        }
        return mask;
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
            fillRect(area, "carvings", x1+2,y1+2,x1+w-3,y1+h-3);
        }
    }


    public void scatterActorsByTags(UArea area, int x1, int y1, int x2, int y2, String[] tags, int level, int amount) {
        ArrayList<String> names = new ArrayList<>();
        for (String tag : tags) {
            String[] thenames = actorCzar.getActorsByTag(tag,level);
            for (String name: thenames) {
                names.add(name);
            }
            System.out.println("SCAPER: got " + Integer.toString(names.size()) + " actor types to scatter");
            if (names.size() < 1)
                return;
        }
        while (amount > 0) {
            amount--;
            String name;
            if (names.size() == 1)
                name = names.get(0);
            else
                name = names.get(random.nextInt(names.size()));
            UActor actor = actorCzar.getActorByName(name);
            UCell dest = getRandomSpawn(area, actor, x1, y1, x2, y2);
            actor.moveToCell(area, dest.x, dest.y);
        }
    }

    public void scatterThingsByTags(UArea area, int x1, int y1, int x2, int y2, String[] tags, int level, int amount) {
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
            UCell dest = getRandomSpawn(area, thing, x1, y1, x2, y2);
            thing.moveToCell(area, dest.x, dest.y);
        }
    }

    /**
     * Link a new region to a spot in this area.
     * @param exittype Stairs to place in the area.
     * @param destlevel Level the stairs go to in the new region.
     * @param backexittype Exit type the region should make going back to us.
     */
    public void linkRegionAt(UArea area, int x, int y, String exittype, URegion region, int destlevel, String backexittype) {
        region.addLink(destlevel, backexittype, area.label);
        commander.cartographer.addRegion(region);
        area.setTerrain(x,y,exittype);
        ((Stairs)area.terrainAt(x,y)).setLabel(region.getId() + " " + Integer.toString(destlevel));
    }

    public String getType() {
        return type;
    }
}