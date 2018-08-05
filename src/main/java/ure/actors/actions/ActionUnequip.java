package ure.actors.actions;

import ure.actors.UActor;
import ure.things.UThing;

public class ActionUnequip extends UAction {

    public static String id = "UNEQUIP";

    UThing thing;

    public ActionUnequip(UActor _actor, UThing _thing) {
        super(_actor);
        thing = _thing;
    }

    @Override
    public void doMe() {
        thing.tryUnequip(actor);
    }
}
