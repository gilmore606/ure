package ure.actors;

import ure.UColor;
import ure.UREArea;
import ure.URELight;
import ure.actors.UREActor;

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
    public void moveToCell(UREArea area, int destX, int destY) {
        super.moveToCell(area,destX,destY);
        if (light != null) {
            light.moveTo(area,destX,destY);
        }

    }
}
