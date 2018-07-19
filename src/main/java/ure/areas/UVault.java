package ure.areas;

/**
 * A vault represents a room template which can be stamped into an area by a Landscaper.
 *
 */
public class UVault {

    public class VSpawn {
        public String name;  // specify a particular thing/actor (or substring match if tag also defined)
        public String tag;   // specify anything from tag (if name is null)
        public int level;    // relative to area level + or -
        public int[] loc;    // xy in room
        public int[] locrange;  // if present, treat as x2,y2 and loc as x1,y1
    }
    public String name;
    public String[] tags;
    public String description;  // printed on room-enter trigger

    public int[] levels;
    public int cols, rows;
    public boolean rotate = true;
    public boolean mirror = true;

    public String[] terrain;
    public VSpawn[] things;
    public VSpawn[] actors;

    public UVault() {

    }

    public void initialize() {
        name = "";
        tags = null;
        description = null;
        terrain = new String[30];
        for (int i=0;i<30;i++) {
            terrain[i] = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        }
        cols = 30;
        rows = 30;
    }
    public int[] getLevels() {
        return levels;
    }

    public void setLevels(int[] levels) {
        this.levels = levels;
    }

    public String getName() { return name; }
    public void setName(String n) { name = n; }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public String[] getTerrain() { return terrain; }
    public void setTerrain(String[] _terrain) { terrain = _terrain; }

    public char terrainCharAt(int x, int y) {
        return terrain[y].charAt(x);
    }

    public void cropSize(int xsize, int ysize) {
        cols = xsize;
        rows = ysize;
    }
}
