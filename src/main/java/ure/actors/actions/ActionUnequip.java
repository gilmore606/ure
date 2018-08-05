package ure.actors.actions;

import org.lwjgl.system.CallbackI;
import ure.actors.UActor;
import ure.actors.UPlayer;
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
        if (thing.tryUnequip(actor)) {
            if (actor instanceof UPlayer) {
                commander.printScroll(thing.getIcon(), "You" + (thing.getEquipSlots()[0].equals("equip") ? " unequip " : " take off ") + thing.getIname() + ".");
            }
        }
    }
}
