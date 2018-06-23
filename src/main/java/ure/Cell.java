package ure;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of an Area
 *
 */
public class Cell {
    URETerrain terrain;
    URECollection contents;
    boolean isSeen = false;

    public Cell(URETerrain theTerrain) {
        contents = new URECollection();
        setTerrain(theTerrain);
    }

    public void setTerrain(URETerrain theTerrain) {
        terrain = theTerrain;
    }

    public URETerrain getTerrain() {
        return terrain;
    }
}
