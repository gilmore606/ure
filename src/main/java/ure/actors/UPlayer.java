package ure.actors;

import ure.areas.UCell;
import ure.math.UColor;
import ure.areas.UArea;
import ure.ui.ULight;
import ure.things.UThing;

/**
 * UPlayer implements a UActor whose actions are initiated by user input.  More than one can exist.
 *
 */
public class UPlayer extends UActor {

    public boolean awake = true;

    ULight light;

    public UPlayer(String thename, char theicon, UColor thecolor, boolean addOutline, UColor selfLightColor, int selfLight, int selfLightFalloff) {
        super();
        initialize();
        setDisplayFields(thename, theicon, thecolor, addOutline);
        if (selfLight > 0) {
            light = new ULight(selfLightColor, selfLightFalloff + selfLight, selfLight);
        }
    }

    @Override
    public void moveToCell(UArea area, int destX, int destY) {
        super.moveToCell(area,destX,destY);
        if (light != null) {
            light.moveTo(area,destX,destY);
        }
        if (area.terrainAt(destX,destY).isBreaklatch())
            commander.latchBreak();

    }

    @Override
    public boolean canSee(UThing thing) {
        int x = thing.areaX();
        int y = thing.areaY();
        if (camera.visibilityAt(x - camera.leftEdge, y - camera.topEdge) > commander.config.getVisibilityThreshold())
            if (camera.lightAt(x-camera.leftEdge,y-camera.topEdge).grayscale() > commander.config.getVisibilityThreshold())
            return true;
        return false;
    }

    @Override
    public void walkFail(UCell cell) {
        super.walkFail(cell);
        commander.latchBreak();
    }

    @Override
    public int bounceAnimY() {
        return 0;
    }

    @Override
    public void closeOut() {

    }
}
