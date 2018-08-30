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
    public boolean areaUnique, gameUnique;
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
        name = "?";
        initialize(6,6);
    }
    public void initialize(int xsize, int ysize) {
        cols = xsize;
        rows = ysize;
        terrain = new String[cols][rows];
        for (int i=0;i<cols;i++) {
            for (int j=0;j<rows;j++) {
                terrain[i][j] = "null";
            }
        }
    }

    public String[] getTags() { return tags; }
    public void setTags(String[] s) { tags = s; }
    public boolean isAreaUnique() { return areaUnique; }
    public void setAreaUnique(boolean b) { areaUnique = b; }
    public boolean isGameUnique() { return gameUnique; }
    public void setGameUnique(boolean b) { gameUnique = b; }
    public String getDescription() { return description; }
    public void setDescription(String s) { description = s; }
    public boolean isRotate() { return rotate; }
    public void setRotate(boolean b) { rotate = b; }
    public boolean isMirror() { return mirror; }
    public void setMirror(boolean b) { mirror = b; }
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
