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

    public void initialize() {
        name = "";
        tags = null;
        description = null;
        terrain = new String[30][30];
        for (int i=0;i<30;i++) {
            for (int j=0;j<30;j++) {
                terrain[i][j] = "null";
            }
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

    public String[][] getTerrain() { return terrain; }
    public void setTerrain(String[][] _terrain) { terrain = _terrain; }

    public String terrainAt(int x, int y) {
        return terrain[x][y];
    }

    public void cropSize(int xsize, int ysize) {
        cols = xsize;
        rows = ysize;
    }
}
