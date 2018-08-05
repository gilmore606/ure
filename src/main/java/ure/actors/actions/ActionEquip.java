package ure.actors.actions;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.things.UThing;

public class ActionEquip extends UAction {

    public static String id = "EQUIP";

    UThing thing;

    public ActionEquip(UActor _actor, UThing _thing) {
        super(_actor);
        thing = _thing;
    }

    @Override
    public void doMe() {
        String[] slots = thing.getEquipSlots();
        int slotcount = thing.getEquipSlotCount();
        for (String slot : slots) {
            if (!actor.getBody().hasPart(slot)) {
                if (actor instanceof UPlayer) commander.printScroll("You're short a " + slot + " to equip that on.");
                return;
            }
        }

        for (String slot : slots) {
            if (!actor.freeEquipSlot(slot,slotcount)) {
                if (actor instanceof UPlayer) commander.printScroll("You couldn't make room for it.");
                return;
            }
        }
        if (actor.tryEquipThing(thing)) {
            if (actor instanceof UPlayer) {
                commander.printScroll(thing.getIcon(),"You" + (slots[0].equals("equip") ? " equip " : " wear ") + thing.getIname() + ".");
            } else {
                commander.printScrollIfSeen(actor, actor.getDname() + " equips " + thing.getIname() + ".");
            }
        }
    }
}
