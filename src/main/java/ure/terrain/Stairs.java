package ure.terrain;

import ure.UREArea;

public class Stairs extends TerrainI implements URETerrain {

    public static final String TYPE = "stairs";

    UREArea destArea;
    int destX, destY;
    boolean onstep = false;
    boolean confirm = true;

}
