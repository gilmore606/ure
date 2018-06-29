package ure;

public class UREPlayer extends UREActor {

    URELight light;

    public UREPlayer(String thename, char theicon, UColor thecolor, boolean addOutline, int selfLight, int selfLightFalloff) {
        super(thename, theicon, thecolor, addOutline);
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
