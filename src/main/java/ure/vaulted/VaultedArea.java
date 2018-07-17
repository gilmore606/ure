package ure.vaulted;

import ure.actions.UAction;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.sys.UCommander;
import ure.things.UThing;

public class VaultedArea extends UArea {

    public VaultedArea(int thexsize, int theysize) {
        super(thexsize, theysize, "null");
        label = "vaulted";
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
    public void hearTimeTick(UCommander cmdr) {
        adjustSunColor(720);
    }

    @Override
    public void broadcastEvent(UAction action) {

    }


}
