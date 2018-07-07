package ure.actors;

import ure.math.UColor;
import ure.UArea;
import ure.ULight;
import ure.things.UThing;

public class UPlayer extends UActor {

    public boolean awake = true;

    public static boolean isActor = true;

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
    public boolean isPlayer() { return true; }

    @Override
    public void moveToCell(UArea area, int destX, int destY) {
        super.moveToCell(area,destX,destY);
        if (light != null) {
            light.moveTo(area,destX,destY);
        }

    }

    @Override
    public boolean canSee(UThing thing) {
        int x = thing.areaX();
        int y = thing.areaY();
        if (camera.visibilityAt(x - camera.leftEdge, y - camera.topEdge) > 0.1f)
            return true;
        return false;
    }
}
