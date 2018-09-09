package ure.areas.gen;

import ure.areas.UArea;

public class Metascaper extends ULandscaper {

    public static final String TYPE = "meta";
    Shape shaper;
    String wallTerrain, floorTerrain;
    boolean pruneDeadEnds, wipeSmallRegions, roundCorners;

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe(wallTerrain);
        if (pruneDeadEnds)
            shaper.pruneDeadEnds();
        if (wipeSmallRegions)
            shaper.wipeSmallRegions();
        if (roundCorners)
            shaper.roundCorners();
        shaper.writeTerrain(area, floorTerrain, 1, 1);
    }

    public void setup(Shape shaper, String wallTerrain, String floorTerrain, boolean pruneDeadEnds, boolean wipeSmallRegions, boolean roundCorners) {
        this.shaper = shaper;
        this.wallTerrain = wallTerrain;
        this.floorTerrain = floorTerrain;
        this.pruneDeadEnds = pruneDeadEnds;
        this.wipeSmallRegions = wipeSmallRegions;
        this.roundCorners = roundCorners;
    }
}
