package ure.areas.gen;

import ure.areas.UArea;
import ure.areas.gen.shapers.Shaper;

import java.util.ArrayList;

public class Layer {
    public Shaper shaper;
    public float density;
    public int printMode;
    public static final int PRINTMODE_ALL = 0;
    public static final int PRINTMODE_WALLS = 1;
    public static final int PRINTMODE_FLOORS = 2;
    public static final int PRINTMODE_STRUCTURE = 3;
    public String terrain;
    public boolean pruneDeadEnds, wipeSmallRegions, roundCorners, invert;

    public void update(Shaper shaper, float density, int printMode, String terrain, boolean pruneDeadEnds, boolean wipeSmallRegions, boolean roundCorners, boolean invert) {
        this.shaper = shaper;
        this.density = density;
        this.printMode = printMode;
        this.terrain = terrain;
        this.pruneDeadEnds = pruneDeadEnds;
        this.wipeSmallRegions = wipeSmallRegions;
        this.roundCorners = roundCorners;
        this.invert = invert;
    }
    public void build() {
        shaper.build();
        if (pruneDeadEnds)
            shaper.pruneDeadEnds();
        if (wipeSmallRegions)
            shaper.wipeSmallRegions();
        if (roundCorners)
            shaper.roundCorners();
        if (invert)
            shaper.invert();
        shaper.pruneRooms();
    }
    public void print(UArea area, int xoffset, int yoffset) { print(area,xoffset,yoffset,null); }
    public void print(UArea area, int xoffset, int yoffset, String wallTerrain) {
        for (int i=0;i<shaper.xsize;i++) {
            for (int j=0;j<shaper.ysize;j++) {
                if (shaper.value(i,j)) {
                    if (density >= 1f || shaper.random.f() < density) {
                        if ((printMode == PRINTMODE_ALL || printMode == PRINTMODE_STRUCTURE)
                                ||  (printMode == PRINTMODE_WALLS && !area.cellAt(i+xoffset,j+yoffset).terrain().isPassable())
                                ||  (printMode == PRINTMODE_FLOORS && area.cellAt(i+xoffset,j+yoffset).terrain().isPassable())) {
                            area.setTerrain(i+xoffset,j+yoffset,terrain);
                        }
                    }
                } else if (printMode == PRINTMODE_STRUCTURE) {
                    if (shaper.value(i-1,j-1)||shaper.value(i-1,j)||shaper.value(i-1,j+1)
                    ||shaper.value(i,j-1)||shaper.value(i,j+1)
                    ||shaper.value(i+1,j-1)||shaper.value(i+1,j)||shaper.value(i+1,j+1)) {
                        area.setTerrain(i+xoffset,j+yoffset,wallTerrain);
                    }
                }
            }
        }
    }
    public ArrayList<Shaper.Room> rooms() { return shaper.rooms; }
}