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
    public int[] levels;
    public int cols, rows;
    public boolean rotate = true;
    public boolean mirror = true;

    public String[] terrain;
    public VSpawn[] things;
    public VSpawn[] actors;
}
