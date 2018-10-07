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
    public static final int PRINTMODE_NONE = 3;
    public String terrain;
    public boolean pruneDeadEnds, roundCorners, invert;

    public Layer() {

    }

    public void update(Shaper shaper, float density, int printMode, String terrain, boolean pruneDeadEnds, boolean wipeSmallRegions, boolean roundCorners, boolean invert) {
        this.shaper = shaper;
        this.density = density;
        this.printMode = printMode;
        this.terrain = terrain;
        this.pruneDeadEnds = pruneDeadEnds;
        this.roundCorners = roundCorners;
        this.invert = invert;
    }

    public void build(Layer previousLayer, UArea area) {
        shaper.build(previousLayer, area);
        if (pruneDeadEnds)
            shaper.pruneDeadEnds();
        if (roundCorners)
            shaper.roundCorners();
        if (invert)
            shaper.invert();
        shaper.pruneRooms();
    }

    public void print(UArea area, int xoffset, int yoffset) {
        for (int i=0;i<shaper.xsize;i++) {
            for (int j=0;j<shaper.ysize;j++) {
                if (shaper.value(i,j)) {
                    if (density >= 1f || shaper.random.f() < density) {
                        if ((printMode == PRINTMODE_ALL)
                                || (printMode == PRINTMODE_WALLS && !area.cellAt(i + xoffset, j + yoffset).terrain().passable())
                                || (printMode == PRINTMODE_FLOORS && area.cellAt(i + xoffset, j + yoffset).terrain().passable())) {
                            area.setTerrain(i + xoffset, j + yoffset, terrain);
                        }
                    }
                }
            }
        }
    }
    public ArrayList<Room> rooms() { return shaper.rooms; }

    public Shaper getShaper() {
        return shaper;
    }

    public void setShaper(Shaper shaper) {
        this.shaper = shaper;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getPrintMode() {
        return printMode;
    }

    public void setPrintMode(int printMode) {
        this.printMode = printMode;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public boolean isPruneDeadEnds() {
        return pruneDeadEnds;
    }

    public void setPruneDeadEnds(boolean pruneDeadEnds) {
        this.pruneDeadEnds = pruneDeadEnds;
    }

    public boolean isRoundCorners() {
        return roundCorners;
    }

    public void setRoundCorners(boolean roundCorners) {
        this.roundCorners = roundCorners;
    }

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }
}