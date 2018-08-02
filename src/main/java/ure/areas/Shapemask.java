package ure.areas;

import ure.sys.Injector;
import ure.sys.UCommander;
import ure.things.UThing;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A Shapemask is a 1-bit 2D array of cells.  Landscaper methods can generate these to use for stamping terrain into areas.
 *
 * Shapemask has many public methods for warping, evolving, and otherwise changing the shape it contains.  You can combine these
 * methods in your Landscapers to build interesting terrain.
 *
 */
public class Shapemask {

    @Inject
    UCommander commander;

    public int xsize, ysize;
    public boolean[][] cells;
    boolean[][] buffer;

    public static final int MASKTYPE_OR = 0;
    public static final int MASKTYPE_AND = 1;
    public static final int MASKTYPE_XOR = 2;

    public Shapemask(int _xsize, int _ysize) {
        Injector.getAppComponent().inject(this);
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
    public void clear() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                cells[x][y] = false;
    }
    public void fill() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                cells[x][y] = true;
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
    public void noiseWipe(float density) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (commander.random.nextFloat() < density) write(x,y,true);
                else write(x,y,false);
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
    public void maskWith(Shapemask mask, int masktype) { maskWith(mask, masktype, 0, 0); }
    public void maskWith(Shapemask mask, int masktype, int xoffset, int yoffset) {
        for (int x=0;x<mask.xsize;x++) {
            for (int y=0;y<mask.ysize;y++) {
                boolean src = mask.value(x,y);
                boolean dst = value(x+xoffset,y+yoffset);
                switch (masktype) {
                    case MASKTYPE_OR:   dst = (dst || src);
                        break;
                    case MASKTYPE_AND:  dst = (dst && src);
                        break;
                    case MASKTYPE_XOR:  dst = (dst || src) && !(dst && src);
                        break;
                }
                write(x+xoffset,y+yoffset,dst);
            }
        }
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
    public void writeTerrain(UArea area, String terrain, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y))
                    area.setTerrain(x+xoffset,y+yoffset,terrain);
    }

    /**
     * Write a thing into every true cell of this mask in the area
     */
    public void writeThings(UArea area, String thing, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y)) {
                    UThing t = commander.thingCzar.getThingByName(thing);
                    t.moveToCell(area,x+xoffset,y+yoffset);
                }
    }
    /**
     * Read a terrain type from a real area as a mask
     */
    public void readTerrain(UArea area, String terrain, int xoffset, int yoffset) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (area.terrainAt(x+xoffset,y+yoffset).getName().equals(terrain))
                    write(x,y,true);
    }

    /**
     * Make and return a copy of this mask
     */
    public Shapemask copy() {
        Shapemask copy = new Shapemask(xsize, ysize);
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                copy.cells[x][y] = cells[x][y];
        return copy;
    }

    /**
     * Flip true-false on all cells of this mask
     */
    public void invert() {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                cells[x][y] = !cells[x][y];
    }

    /**
     * Grow outward from all edges
     */
    public void grow(int repeats, int threshold) {
        for (int i=0;i<repeats;i++) {
            for (int x=0;x<xsize;x++)
                for (int y=0;y<ysize;y++)
                    if (value(x,y) || (neighbors(x,y) >= threshold))
                        writeBuffer(x,y,true);
                    else
                        writeBuffer(x,y,false);
            printBuffer();
        }
    }

    /**
     * Shrink inward from all edges
     */
    public void shrink(int repeats, int threshold) {
        for (int i=0;i<repeats;i++) {
            for (int x=0;x<xsize;x++)
                for (int y=0;y<ysize;y++)
                    if (value(x,y) && (neighbors(x,y) >= threshold))
                        writeBuffer(x,y,true);
                    else
                        writeBuffer(x,y,false);
            printBuffer();
        }
    }

    /**
     * Randomly thin out true cells to false.
     */
    public void noiseThin(float density) {
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y))
                    if (commander.random.nextFloat() > density)
                        clear(x,y);
    }

    /**
     * Keep only cells N spaces apart from other true cells
     */
    public void sparsen(int minspace, int maxspace) {
        clearBuffer();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y)) {
                    writeBuffer(x,y,true);
                    int radius = (minspace + commander.random.nextInt(maxspace-minspace))/2;
                    for (int dx=-radius;dx<radius;dx++)
                        for (int dy=-radius;dy<radius;dy++)
                            if (!valueBuffer(x+dx,y+dy))
                                write(x+dx,y+dy,false);
                }
    }

    /**
     * Select only edge cells
     */
    public void edges() {
        clearBuffer();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y) && neighborsPrime(x,y) < 4)
                    writeBuffer(x,y,true);
        printBuffer();
    }

    /**
     * Select only edge cells, including diagonal corners
     */
    public void edgesThick() {
        clearBuffer();
        for (int x=0;x<xsize;x++)
            for (int y=0;y<ysize;y++)
                if (value(x,y) && neighbors(x,y) < 8)
                    writeBuffer(x,y,true);
        printBuffer();
    }

    /**
     * 'Jumble' cells with a CA-type method
     */
    public void jumble(int nearDensity, int farDensity, int passes) {
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
    }

    /**
     * Smooth out noise and leave continuous shapes
     */
    public void smooth(int density, int passes) {
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
            int i = commander.random.nextInt(cells.size());
            results[n] = cells.get(i);
            cells.remove(i);
        }
        return results;
    }

    /**
     * Are all true cells connected in a single contiguous region?
     */
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
    public ArrayList<Shapemask> regions() {
        backupToBuffer();
        ArrayList<Shapemask> regions = new ArrayList<>();
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (value(x,y)) {
                    Shapemask region = copy();
                    region.flood(x,y,false);
                    region.maskWith(this, MASKTYPE_XOR);
                    maskWith(region, MASKTYPE_AND);
                    regions.add(region);
                }
            }
        }
        printBuffer();
        return regions;
    }

    /**
     * Flood-fill at x,y with val
     */
    public void flood(int x, int y, boolean val) {
        if (value(x,y) == val) return;
        LinkedList<int[]> q = new LinkedList<>();
        q.push(new int[]{x,y});
        while (!q.isEmpty()) {
            int[] n = q.pop();
            int wx = n[0];
            int ex = n[0];
            while (value(wx,n[1]) != val) { wx--; }
            while (value(ex,n[1]) != val) { ex++; }
            for (int i=wx+1;i<ex;i++) {
                write(i,n[1],val);
                if (value(i,n[1]-1) != val) q.push(new int[]{i,n[1]-1});
                if (value(i,n[1]+1) != val) q.push(new int[]{i,n[1]+1});
            }
        }
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
}
