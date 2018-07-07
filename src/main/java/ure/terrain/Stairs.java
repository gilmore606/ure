package ure.terrain;

import ure.UArea;

public class Stairs extends TerrainI implements UTerrain {

    public static final String TYPE = "stairs";

    UArea destArea;
    int destX, destY;
    boolean onstep = false;
    boolean confirm = true;

}
