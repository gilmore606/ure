package ure.actors;

import ure.UColor;
import ure.UREArea;
import ure.URELight;
import ure.URERendererOGL;
import ure.actors.UREActor;
import ure.things.UREThing;

public class UREPlayer extends UREActor {

    public static boolean isActor = true;

    URELight light;

    public UREPlayer(String thename, char theicon, UColor thecolor, boolean addOutline, int selfLight, int selfLightFalloff) {
        super();
        initialize();
        setDisplayFields(thename, theicon, thecolor, addOutline);
        if (selfLight > 0) {
            light = new URELight(UColor.COLOR_WHITE, selfLightFalloff + selfLight, selfLight);
        }
    }

    @Override
    public boolean isPlayer() { return true; }

    @Override
    public void moveToCell(UREArea area, int destX, int destY) {
        super.moveToCell(area,destX,destY);
        if (light != null) {
            light.moveTo(area,destX,destY);
        }

    }

    @Override
    public boolean canSee(UREThing thing) {
        int x = thing.areaX();
        int y = thing.areaY();
        if (camera.visibilityAt(x - camera.x1,y - camera.y1) > 0.1f)
            return true;
        return false;
    }
}
