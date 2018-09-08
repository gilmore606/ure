package ure.areas.gen;

import ure.areas.UArea;

public class Metascaper extends ULandscaper {

    public static final String TYPE = "meta";
    Shape shaper;
    String wallTerrain, floorTerrain;

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe(wallTerrain);
        shaper.writeTerrain(area, floorTerrain, 0, 0);
    }

    public void setup(Shape shaper, String wallTerrain, String floorTerrain) {
        this.shaper = shaper;
        this.wallTerrain = wallTerrain;
        this.floorTerrain = floorTerrain;
    }
}
