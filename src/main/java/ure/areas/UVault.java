package ure.areas;

import ure.actors.SpawnActor;
import ure.things.SpawnItem;

/**
 * A vault represents a room template which can be stamped into an area by a Landscaper.
 *
 */
public class UVault {

    public String name;
    public String[] tags;
    public String description;  // printed on room-enter trigger

    public int[] levels;
    public int cols, rows;
    public boolean rotate = true;
    public boolean mirror = true;

    public String[][] terrain;
    public SpawnItem[] things;
    public SpawnActor[] actors;

    public UVault() {

    }

    public void initialize() { initialize(30,30); }
    public void initialize(int xsize, int ysize) {
        cols = xsize;
        rows = ysize;
        name = "";
        tags = null;
        description = null;
        terrain = new String[cols][rows];
        for (int i=0;i<cols;i++) {
            for (int j=0;j<rows;j++) {
                terrain[i][j] = "null";
            }
        }
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

    public String[][] getTerrain() { return terrain; }
    public void setTerrain(String[][] _terrain) { terrain = _terrain; }

    public String terrainAt(int x, int y) {
        if (terrain[x][y] == null)
            return "null";
        return terrain[x][y];
    }
    public void setTerrainAt(int x, int y, String t) {
        terrain[x][y] = t;
    }

}
