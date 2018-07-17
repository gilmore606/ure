package ure.vaulted;

import ure.actions.UAction;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.math.UColor;
import ure.sys.UCommander;
import ure.things.UThing;

public class VaultedArea extends UArea {

    public VaultedArea(int thexsize, int theysize) {
        super(thexsize, theysize, "null");
        label = "vaulted";
        resetSunColorLerps();
        addSunColorLerp(0, UColor.COLOR_WHITE);
        addSunColorLerp(24*60, UColor.COLOR_WHITE);
        setSunColor(1f,1f,1f);
    }

    @Override
    public boolean willAcceptThing(UThing thing, int x, int y) {
        if (isValidXY(x, y)) {
            if (thing instanceof UPlayer) {
                return true;
            }
        }
        return super.willAcceptThing(thing,x,y);
    }

    @Override
    public void wakeCheckAll(int playerx, int playery) {

    }

    @Override
    public void broadcastEvent(UAction action) {

    }


}
