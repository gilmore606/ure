package ure.areas.gen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UArea;
import ure.math.URandom;
import ure.sys.Injector;
import ure.things.UThing;
import ure.things.UThingCzar;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A Shapemask is a 1-bit 2D array of cells.  Landscaper methods can generate these to use for stamping terrain into areas.
 *
 * Shapemask has many public methods for warping, evolving, and otherwise changing the shape it contains.  You can combine these
 * methods in your Landscapers to build interesting terrain.
 *
 */
public class Shape {

    @JsonIgnore
    @Inject
    public URandom random;

    @JsonIgnore
    @Inject
    UThingCzar thingCzar;

    public int xsize, ysize;
    @JsonIgnore
    public int cellCount;
    @JsonIgnore
    public boolean[][] cells;
    @JsonIgnore
    public boolean[][] buffer;

    public static final int MASK_OR = 0;
    public static final int MASK_AND = 1;
    public static final int MASK_XOR = 2;
    public static final int MASK_NOT = 3;

    public class Room {
        public int x,y,width,height;
        public Room(int x, int y, int width, int height) {
            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
        }
        public Room(int width, int height) {
            this.width=width;
            this.height=height;
            this.x=-1;
            this.y=-1;
        }
        public Face[] faces() {
            Face[] faces = new Face[4];
            faces[0] = new Face(x,y-1,width,0,-1);
            faces[1] = new Face(x+width,y,height,1,0);
            faces[2] = new Face(x,y+height,width,0,1);
            faces[3] = new Face(x-1,y,height,-1,0);
            return faces;
        }
        public void rotate() {
            int tmp = width;
            width = height;
            height = tmp;
        }
        public void print(Shape space) { print(space, false); }
        public void print(Shape space, boolean rounded) {
            for (int xi=0;xi<width;xi++) {
                for (int yi = 0;yi < height;yi++) {
                    if (!rounded || !(xi==0 || xi==width-1) || !(yi==0 || yi==height-1))
                        space.set(x + xi, y + yi);
                }
            }
        }
        public void punchDoors(Shape space) { punchDoors(space, false); }
        public void punchDoors(Shape space, boolean punchAll) {
            for (Face face : faces()) {
                face.punchDoors(space, punchAll);
            }
        }
        public boolean isHallway() {
            if ((width*3<height) || (height*3<width))
                return true;
            return false;
        }
        public boolean unobstructed(UArea area) {
            if (area.terrainAt(x+1,y+1) != null)
                return area.terrainAt(x+1,y+1).passable();
            return false;
        }
        public boolean unobstructed() { return value(x+1,y+1); }

        public boolean touches(Room r) {
            if (x > r.x+r.width || y > r.y+r.height || x+width < r.x || y+height < r.y)
                return false;
            return true;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public class Face {
        public int x, y, length;
        int facex, facey;

        public Face(int x, int y, int length, int facex, int facey) {
            this.x = x;
            this.y = y;
            this.length = length;
            this.facex = facex;
            this.facey = facey;
        }

        /**
         * Try to add the room somewhere along me.
         * If we can, record the room's xy and return it, else return null.
         */
        public Room addRoom(Room room, Shape space) {
            ArrayList<Integer> spaces = new ArrayList<>();
            for (int i = -(room.width - 3);i < length - 3;i++) {
                boolean blocked = false;
                for (int cx = -1;cx < room.width + 1;cx++) {
                    for (int cy = 0;cy < room.height + 2;cy++) {
                        int tx = transX(cx + i, cy);
                        int ty = transY(cx + i, cy);
                        if (space.value(tx, ty) || !space.isValidXY(tx, ty)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked) break;
                }
                if (!blocked) spaces.add(i);
            }
            if (spaces.size() == 0) return null;
            int i = (int) random.member((List) spaces);
            room.x = transX(i, 1);
            room.y = transY(i, 1);
            if (facey == -1) {
                room.y -= room.height - 1;
            } else if (facex == -1) {
                room.rotate();
                room.x -= room.width - 1;
            } else if (facex == 1) {
                room.rotate();
            }
            return room;
        }

        /**
         * Translate coordinates from my relative space to absolute space.
         */
        public int transX(int dx, int dy) {
            if (facey == -1 || facey == 1) {
                return x + dx;
            } else if (facex == 1) {
                return x + dy;
            } else {
                return x - dy;
            }
        }

        public int transY(int dx, int dy) {
            if (facey == -1) {
                return y - dy;
            } else if (facey == 1) {
                return y + dy;
            } else {
                return y + dx;
            }
        }

        /**
         * Punch a doorhole somewhere along us, if possible.
         */
        public void punchDoors(Shape space) {
            punchDoors(space, false);
        }

        public void punchDoors(Shape space, boolean punchAll) {
            for (int i : random.seq(length)) {
                int fx = x + (i * Math.abs(facey));
                int fy = y + (i * Math.abs(facex));
                if (space.value(fx + facex, fy + facey) && space.value(fx - facex, fy - facey)) {
                    space.set(fx, fy);
                    if (!punchAll) return;
                }
            }
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getFacex() {
            return facex;
        }

        public void setFacex(int facex) {
            this.facex = facex;
        }

        public int getFacey() {
            return facey;
        }

        public void setFacey(int facey) {
            this.facey = facey;
        }
    }

    public Shape() {
        Injector.getAppComponent().inject(this);
    }
    public Shape(int _xsize, int _ysize) {
        this();
        resize(_xsize, _ysize);
    }

    public void resize(int _xsize, int _ysize) {
        xsize = _xsize;
        ysize = _ysize;
        cells = new boolean[xsize][ysize];
        buffer = new boolean[xsize][ysize];
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                cells[x][y] = false;
                buffer[x][y] = false;
            }
        }
    }

    public boolean isValidXY(int x, int y) {
        if (x < 0 || y < 0) return false;
        if (x >= xsize || y >= ysize) return false;
        return true;
    }

    /**
     * Calculate the 0f-1f density of this mask
     */
    public float density() {
        int total = 0;
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (cells[x][y])
                    total++;
        return (float)total / (float)(xsize*ysize);
    }

    public void set(int x, int y) {
        if (isValidXY(x,y))
            cells[x][y] = true;
    }
    public void clear(int x, int y) {
        if (isValidXY(x,y))
            cells[x][y] = false;
    }
    public void write(int x, int y, boolean val) {
        if (isValidXY(x,y))
            cells[x][y] = val;
    }

    /**
     * write a cell of the internal scratch buffer
     */
    public void writeBuffer(int x, int y, boolean val) {
        if (isValidXY(x,y))
            buffer[x][y] = val;
    }

    public boolean value(int x, int y) {
        if (isValidXY(x,y))
            return cells[x][y];
        return false;
    }
    public boolean valueBuffer(int x, int y) {
        if (isValidXY(x,y))
            return buffer[x][y];
        return false;
    }
    public Shape clear() {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++) {
                cells[x][y] = false;
            }
        }
        return this;
    }
    public Shape fill() {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++) {
                cells[x][y] = true;
            }
        }
        return this;
    }
    public void fillRect(int x1, int y1, int x2, int y2) {
        for (int x=x1;x<=x2;x++)
            for (int y=y1;y<=y2;y++)
                set(x,y);
    }
    public void clearBuffer() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                buffer[x][y] = false;
    }
    public void fillBuffer() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                buffer[x][y] = true;
    }
    public Shape noiseWipe(float density) {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++) {
                if (random.f() < density) write(x, y, true);
                else write(x, y, false);
            }
        }
        return this;
    }
    // TODO: write simplexWipe
    public Shape simplexWipe() {

        return this;
    }

    /**
     * neighbor count at x,y -- not including the point itself
     */
    public int neighbors(int x, int y) { return neighbors(x,y,1); }

    /**
     * neighbor count at x,y at manhattan-distance d
     */
    public int neighbors(int x, int y, int d) {
        if (!isValidXY(x,y)) return 0;
        int n = 0;
        for (int dx=-d;dx<=d;dx++) {
            for (int dy = -d;dy <= d;dy++) {
                if (dx != 0 || dy != 0) {
                    if (value(x + dx, y + dy))
                        n++;
                }
            }
        }
        return n;
    }

    /**
     * neighbor count in cardinal directions only
     */
    public int neighborsPrime(int x, int y) {
        if (!isValidXY(x,y))
            return 0;
        int n = 0;
        if (value(x+1,y)) n++;
        if (value(x-1,y)) n++;
        if (value(x,y+1)) n++;
        if (value(x,y-1)) n++;
        return n;
    }

    /**
     * Combine with another mask using MASKTYPE_X constants (OR, AND, XOR)
     */
    public Shape maskWith(Shape mask, int masktype) { return maskWith(mask, masktype, 0, 0); }
    public Shape maskWith(Shape mask, int masktype, int xoffset, int yoffset) {
        for (int x=0;x<mask.xsize;x++) {
            for (int y=0;y<mask.ysize;y++) {
                boolean src = mask.value(x,y);
                boolean dst = value(x+xoffset,y+yoffset);
                switch (masktype) {
                    case MASK_OR:   dst = (dst || src);
                        break;
                    case MASK_AND:  dst = (dst && src);
                        break;
                    case MASK_XOR:  dst = (dst || src) && !(dst && src);
                        break;
                    case MASK_NOT:  dst = dst && !src;
                        break;
                }
                write(x+xoffset,y+yoffset,dst);
            }
        }
        return this;
    }

    /**
     * Does the given mask have any true cells that touch my true cells (at the offset position)?
     */
    public boolean touches(Shape mask, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (mask.value(x+xoffset,y+yoffset) && value(x,y))
                    return true;
            }
        }
        return false;
    }
    public boolean touches(Shape mask, int xoffset, int yoffset, Shape ignoremask) {
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (mask.value(x+xoffset,y+yoffset) && value(x,y) && !ignoremask.value(x+xoffset,y+yoffset))
                    return true;
            }
        }
        return false;
    }

    /**
     * Write the internal scratch buffer into the actual cells
     */
    public void printBuffer() {
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                cells[x][y] = buffer[x][y];
            }
        }
    }

    /**
     * Write shape to internal scratch buffer
     */
    public void backupToBuffer() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                buffer[x][y] = cells[x][y];
    }

    /**
     * Write this mask into a real area as terrain
     */
    public void writeTerrain(UArea area, String terrain, int xoffset, int yoffset) { writeTerrain(area,terrain,xoffset,yoffset,1f); }
    public void writeTerrain(UArea area, String terrain, int xoffset, int yoffset, float density) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y))
                    if (density >= 1f || random.f() < density)
                        area.setTerrain(x+xoffset,y+yoffset,terrain);
    }

    /**
     * Write a thing into every true cell of this mask in the area
     */
    public void writeThings(UArea area, String thing, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y)) {
                    UThing t = thingCzar.getThingByName(thing);
                    t.moveToCell(area,x+xoffset,y+yoffset);
                }
    }
    /**
     * Read a terrain type from a real area as a mask
     */
    public Shape readTerrain(UArea area, String terrain, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++) {
                if (area.terrainAt(x + xoffset, y + yoffset).getName().equals(terrain)) {
                    write(x, y, true);
                }
            }
        }
        return this;
    }

    /**
     * Make and return a copy of this mask
     */
    public Shape copy() {
        Shape copy = new Shape(xsize, ysize);
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                copy.cells[x][y] = cells[x][y];
        return copy;
    }

    public void copyTo(Shape dest) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                dest.write(x,y,value(x,y));
    }

    /**
     * Flip true-false on all cells of this mask
     */
    public Shape invert() {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++) {
                cells[x][y] = !cells[x][y];
            }
        }
        return this;
    }

    /**
     * Grow outward from all edges
     */
    public Shape grow(int repeats) {
        for (int i=0;i<repeats;i++) {
            for (int x=0;x<xsize;x++)
                for (int y=0;y<ysize;y++)
                    if (value(x,y) || (neighbors(x,y) >= 2))
                        writeBuffer(x,y,true);
                    else
                        writeBuffer(x,y,false);
            printBuffer();
        }
        return this;
    }

    /**
     * Shrink inward from all edges
     */
    public Shape shrink(int repeats) {
        for (int i=0;i<repeats;i++) {
            for (int x=0;x<xsize;x++)
                for (int y=0;y<ysize;y++)
                    if (value(x,y) && (neighbors(x,y) >= 7))
                        writeBuffer(x,y,true);
                    else
                        writeBuffer(x,y,false);
            printBuffer();
        }
        return this;
    }

    /**
     * Randomly thin out true cells to false.
     */
    public Shape noiseThin(float density) {
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++)
                if (value(x, y))
                    if (random.f() > density)
                        clear(x, y);
        }
        return this;
    }

    /**
     * Keep only cells N spaces apart from other true cells
     */
    public Shape sparsen(int minspace, int maxspace) {
        clearBuffer();
        for (int x=0;x<xsize;x++) {
            for (int y = 0;y < ysize;y++)
                if (value(x, y)) {
                    writeBuffer(x, y, true);
                    int radius = (minspace + random.i(maxspace - minspace)) / 2;
                    for (int dx = -radius;dx < radius;dx++)
                        for (int dy = -radius;dy < radius;dy++)
                            if (!valueBuffer(x + dx, y + dy))
                                write(x + dx, y + dy, false);
                }
        }
        return this;
    }

    public Shape erode(float rot, int passes) {
        clearBuffer();
        int threshold = 6;
        for (int i=0;i<passes;i++) {
            for (int x=0;x<xsize;x++) {
                for (int y=0;y<ysize;y++) {
                    if (value(x,y)) {
                        int n = neighbors(x,y);
                        if (n >= threshold) {
                            writeBuffer(x,y,true);
                        } else if (random.f() < (1f - rot)) {
                            writeBuffer(x,y,true);
                        } else {
                            writeBuffer(x,y,false);
                        }
                    }
                }
            }
            printBuffer();
        }
        return this;
    }

    /**
     * Select only edge cells
     */
    public Shape edges() {
        clearBuffer();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y) && neighborsPrime(x,y) < 4)
                    writeBuffer(x,y,true);
        printBuffer();
        return this;
    }

    /**
     * Select only edge cells, including diagonal corners
     */
    public Shape edgesThick() {
        clearBuffer();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y) && neighbors(x,y) < 8)
                    writeBuffer(x,y,true);
        printBuffer();
        return this;
    }

    /**
     * 'Jumble' cells with a CA-type method
     */
    public Shape jumble(int nearDensity, int farDensity, int passes) {
        clearBuffer();
        for (int i=0;i<passes;i++) {
            for (int x = 0;x < xsize;x++) {
                for (int y = 0;y < ysize;y++) {
                    int near = neighbors(x, y);
                    int far = neighbors(x, y, 2);
                    if (value(x, y)) {
                        near++;
                        far++;
                    }
                    if (near >= nearDensity || far <= farDensity)
                        writeBuffer(x, y, true);
                    else
                        writeBuffer(x, y, false);
                }
            }
            printBuffer();
        }
        return this;
    }

    /**
     * Smooth out noise and leave continuous shapes
     */
    public Shape smooth(int density, int passes) {
        clearBuffer();
        for (int i=0;i<passes;i++) {
            for (int x=0;x<xsize;x++) {
                for (int y=0;y<ysize;y++) {
                    int n = neighbors(x,y);
                    if (value(x,y)) {
                        n++;
                    }
                    if (n >= density)
                        writeBuffer(x,y,true);
                    else
                        writeBuffer(x,y,false);
                }
            }
            printBuffer();
        }
        return this;
    }

    public Shape roundCorners() {
        boolean[][] pattern = new boolean[][]{{false, true, true},{false,true,true},{false,false,false}};
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (value(x,y))
                    write(x,y,!matchNeighbors(x,y,pattern));
            }
        }
        return this;
    }

    /**
     * Prune dead-end one space hallways
     */
    public Shape pruneDeadEnds() {
        clearBuffer();
        int passes = 30;
        for (int i=0;i<passes;i++) {
            boolean killedone = false;
            for (int x=0;x<xsize;x++) {
                for (int y=0;y<ysize;y++) {
                    if (value(x,y)) {
                        int n = neighborsPrime(x, y);
                        if (n <= 1) {
                            writeBuffer(x, y, false);
                            killedone = true;
                        } else
                            writeBuffer(x,y,true);
                    }
                }
            }
            printBuffer();
            if (!killedone) return this;
        }
        return this;
    }

    /**
     * Pick N random cells of a certain value
     */
    public int[] randomCell(boolean val) { return randomCells(val,1)[0]; }
    public int[][] randomCells(boolean val, int n) {
        int[][] results = new int[n][2];
        ArrayList<int[]> cells = new ArrayList<>();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y) == val)
                    cells.add(new int[]{x,y});
        while (n > 0 && cells.size() > 0) {
            n--;
            int i = random.i(cells.size());
            results[n] = cells.get(i);
            cells.remove(i);
        }
        return results;
    }

    /**
     * Are all true cells connected in a single contiguous region?
     */
    @JsonIgnore
    public boolean isContiguous() {
        backupToBuffer();
        boolean filledOne = false;
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y)) {
                    if (filledOne) {
                        printBuffer();
                        return false;
                    } else {
                        flood(x,y,false);
                        filledOne = true;
                    }
                }
        printBuffer();
        return true;
    }

    /**
     * Find all contiguous self-contained regions
     */
    public ArrayList<Shape> regions() {
        backupToBuffer();
        ArrayList<Shape> regions = new ArrayList<>();
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (value(x,y)) {
                    Shape region = copy();
                    region.cellCount = region.flood(x,y,false);
                    region.maskWith(this, MASK_XOR);
                    maskWith(region, MASK_XOR);
                    regions.add(region);
                }
            }
        }
        printBuffer();
        return regions;
    }

    /**
     * Erase everything but the biggest contiguous region.
     */
    public void wipeSmallRegions() {
        ArrayList<Shape> spots = regions();
        Shape biggest = spots.get(0);
        for (Shape spot : spots) {
            if (spot.cellCount > biggest.cellCount)
                biggest = spot;
        }
        biggest.copyTo(this);
    }

    /**
     * Flood-fill at x,y with val
     */
    public int flood(int x, int y, boolean val) {
        if (value(x,y) == val) return 0;
        LinkedList<int[]> q = new LinkedList<>();
        q.push(new int[]{x,y});
        int count = 0;
        while (!q.isEmpty()) {
            int[] n = q.pop();
            int wx = n[0];
            int ex = n[0];
            while (value(wx,n[1]) != val) { wx--; }
            while (value(ex,n[1]) != val) { ex++; }
            for (int i=wx+1;i<ex;i++) {
                write(i,n[1],val);
                count++;
                if (value(i,n[1]-1) != val) q.push(new int[]{i,n[1]-1});
                if (value(i,n[1]+1) != val) q.push(new int[]{i,n[1]+1});
            }
        }
        return count;
    }

    /**
     * Count how many cells a flood at x,y would fill -- the size of the contiguous space
     */
    public int floodCount(int x, int y, boolean val) {
        int c = 0;
        if (value(x,y) == val) return 0;
        backupToBuffer();
        LinkedList<int[]> q = new LinkedList<>();
        q.push(new int[]{x,y});
        while (!q.isEmpty()) {
            int[] n = q.pop();
            int wx = n[0];
            int ex = n[0];
            while (valueBuffer(wx,n[1]) != val) { wx--; }
            while (valueBuffer(ex,n[1]) != val) { ex++; }
            for (int i=wx+1;i<ex;i++) {
                writeBuffer(i,n[1],val);
                c++;
                if (valueBuffer(i,n[1]-1) != val) q.push(new int[]{i,n[1]-1});
                if (valueBuffer(i,n[1]+1) != val) q.push(new int[]{i,n[1]+1});
            }
        }
        return c;
    }

    public boolean tryToFitRoom(Room room, float angle, int displace, boolean draw) {
        int dxy = (int)Math.rint(Math.cos(angle));
        int dyy = (int)Math.rint(Math.sin(angle));
        int dxx = (int)Math.rint(Math.cos(angle+1.5708f));
        int dyx = (int)Math.rint(Math.sin(angle+1.5708f));
        room.width += 2; room.height += 2;
        float x1 = room.x + displace*dxy;
        float y1 = room.y + displace*dyy;
        x1 -= (dxx * (room.width/2) + dxy);
        y1 -= (dyx * (room.width/2) + dyy);
        boolean blocked = false;
        for (int i=0;i<room.width;i++) {
            for (int j=0;j<room.height;j++) {
                float cx = x1 + dxx*i + dxy*j;
                float cy = y1 + dyx*i + dyy*j;
                if (value((int)(cx),(int)cy)) {
                    blocked = true;
                }
            }
        }
        if (draw && !blocked ) {
            x1 += dxx + dyx;
            y1 += dxy + dyy;
            room.x = (int)x1;
            room.y = (int)y1;
            for (int i=0;i<room.width-2;i++) {
                for (int j=0;j<room.height-2;j++) {
                    float cx = x1 + dxx*i + dxy*j;
                    float cy = y1 + dyx*i + dyy*j;
                    set((int)cx,(int)cy);
                }
            }
            set(room.x + dxy,room.y+dyy);
        }
        return !blocked;
    }

    public Room randomRoom(float smallChance, int smallmin, int smallmax,
                           float bigChance, int bigmin, int bigmax,
                           float hallChance, int halllengthmin, int halllengthmax, int hallwidthmin, int hallwidthmax) {
        float rtype = random.f();
        if (rtype < smallChance)
            return new Room(0,0,random.i(1+smallmax-smallmin)+smallmin, random.i(1+smallmax-smallmin)+smallmin);
        else if (rtype < (bigChance+smallChance))
            return new Room(0,0,random.i(1+bigmax-bigmin)+bigmin, random.i(1+bigmax-bigmin)+bigmin);
        else {
            Room r = new Room(0, 0, random.i(1+halllengthmax - halllengthmin) + halllengthmin, random.i(1+hallwidthmax - hallwidthmin) + hallwidthmin);
            if (random.f() > 0.5f)
                r.rotate();
            return r;
        }
    }

    public boolean matchNeighbors(int x, int y, boolean[][] neighborMask) {
        boolean matched = true;
        for (int i=-1;i<2;i++) {
            for (int j = -1;j < 2;j++)
                if (neighborMask[i+1][j+1] != value(x + i, y + j))
                    matched = false;
        }
        if (matched) return true;
        matched = true;
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++)
                if (neighborMask[-i+1][j+1] != value(x+i,y+j))
                    matched = false;
        }
        if (matched) return true;
        matched = true;
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++)
                if (neighborMask[i+1][-j+1] != value(x+i,y+j))
                    matched = false;
        }
        if (matched) return true;
        matched = true;
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++)
                if (neighborMask[-i+1][-j+1] != value(x+i,y+j))
                    matched = false;
        }
        if (matched) return true;

        return false;
    }

    public boolean matchNeighbors(int x, int y, Boolean[][] neighborMask) {
        boolean[][] n = new boolean[neighborMask.length][neighborMask[0].length];
        for (int i=0;i<neighborMask.length;i++) {
            for (int j = 0;j < neighborMask[0].length;j++)
                n[i][j] = neighborMask[i][j];
        }
        return matchNeighbors(x,y,n);
    }

    public boolean hasNeighborWithin(int x, int y, int distance) {
        for (int i=-distance;i<distance;i++) {
            for (int j=-distance;j<distance;j++) {
                if (i != 0 && j != 0) {
                    if (value(x + i, y + j))
                        return true;
                }
            }
        }
        return false;
    }

    public float fullness() {
        int trues = 0;
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                trues += value(x,y) ? 1 : 0;
            }
        }
        return (float)trues / (float)(xsize*ysize);
    }
    public int count() {
        int trues = 0;
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                trues += value(x,y) ? 1 : 0;
            }
        }
        return trues;
    }

    public ArrayList<int[]> edgeList() {
        ArrayList<int[]> edges = new ArrayList<>();
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (value(x,y))
                    if (!value(x-1,y) || !value(x+1,y) || !value(x,y-1) || !value(x,y+1))
                        edges.add(new int[]{x,y});
            }
        }
        return edges;
    }

    public int getXsize() {
        return xsize;
    }

    public void setXsize(int xsize) {
        this.xsize = xsize;
    }

    public int getYsize() {
        return ysize;
    }

    public void setYsize(int ysize) {
        this.ysize = ysize;
    }

    public int getCellCount() {
        return cellCount;
    }

    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    public boolean[][] getCells() {
        return cells;
    }

    public void setCells(boolean[][] cells) {
        this.cells = cells;
    }

    public boolean[][] getBuffer() {
        return buffer;
    }

    public void setBuffer(boolean[][] buffer) {
        this.buffer = buffer;
    }
}
