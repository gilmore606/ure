package ure.ui.modals;

import ure.actors.Bodypart;
import ure.actors.UActor;
import ure.actors.actions.ActionEquip;
import ure.actors.actions.ActionUnequip;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.things.UThing;

import java.util.ArrayList;

public class UModalEquipment extends UModal implements HearModalEquipPick {

    int xpad,ypad;
    int width,height;
    boolean escapable = true;
    ArrayList<Bodypart> slotsBodyparts;
    ArrayList<UThing> slotsThings;

    int selection;

    public UModalEquipment(UColor _bgColor, int _xpad, int _ypad, UActor actor) {
        super(null, "", _bgColor);
        xpad = _xpad;
        ypad = _ypad;
        height = 0;
        slotsBodyparts = new ArrayList<>();
        slotsThings = new ArrayList<>();
        fillSlots(actor);
        width = 26;
        setDimensions(width+xpad,height+ypad);
    }

    void fillSlots(UActor actor) {
        slotsBodyparts.clear();
        slotsThings.clear();
        for (Bodypart bodypart : actor.getBody().getParts()) {
            for (int i=0;i<bodypart.slots;i++) {
                height += 1;
                slotsBodyparts.add(bodypart);
                slotsThings.add(null);
            }
        }
        for (UThing thing : actor.equipment()) {
            int slotsToFill = thing.getEquipSlotCount();
            for (int i=0;i<slotsThings.size();i++) {
                if (slotsToFill > 0 && slotsBodyparts.get(i).name.equals(thing.equipSlots[0]) && slotsThings.get(i) == null) {
                    slotsThings.set(i,thing);
                    slotsToFill--;
                }
            }
        }
    }

    @Override
    public void drawContent(URenderer renderer) {
        Bodypart lastpart = null;
        for (int i=0;i<slotsThings.size();i++) {
            Bodypart part = slotsBodyparts.get(i);
            if (part != lastpart) {
                drawString(renderer, "on " + part.name + ":", 0+xpad, i+ypad, null, null);
            }
            lastpart = part;
            UThing thing = slotsThings.get(i);
            if (thing != null) {
                drawIcon(renderer, thing.getIcon(), 7+xpad, i+ypad);
                drawString(renderer, thing.getName(), 8+xpad, i+ypad, i == selection ? null : UColor.COLOR_GRAY, i == selection ? commander.config.getHiliteColor() : null);
            } else {
                if (i == selection) {
                    drawString(renderer, "        ", 8+xpad, i+ypad, null, commander.config.getHiliteColor());
                }
            }
        }
        int detailX = 17;
        UThing thing = slotsThings.get(selection);
        showDetail(renderer, thing, detailX+xpad, ypad);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("MOVE_N")) {
                selection = cursorMove(selection, -1, slotsThings.size());
            } else if (command.id.equals("MOVE_S")) {
                selection = cursorMove(selection, 1, slotsThings.size());
            } else if (command.id.equals("PASS")) {
                selectSlot();
            } else if (command.id.equals("ESC") && escapable) {
                escape();
            }
        }
    }

    public void selectSlot() {
        ArrayList<UThing> possible = new ArrayList<>();
        ArrayList<UThing> equipment = commander.player().getContents().getThings();
        for (UThing thing : equipment) {
            if (thing.fitsOnBodypart(slotsBodyparts.get(selection).getName()))
                if (thing == slotsThings.get(selection) || !thing.equipped)
                    possible.add(thing);
        }
        possible.add(null);
        if (possible.size() > 0) {
            UModalEquipPick emodal = new UModalEquipPick(null, 1, 1, possible, slotsThings.get(selection), true, true, this, "equip");
            commander.showModal(emodal);
        }
    }

    public void hearModalEquipPick(String context, UThing pick) {
        if (context.equals("unequip")) {
            if (pick != null)
                commander.player().doAction(new ActionUnequip(commander.player(), pick));
        } else {
            if (pick == slotsThings.get(selection))
                return;
            else if (slotsThings.get(selection) != null)
                commander.player().doAction(new ActionUnequip(commander.player(), slotsThings.get(selection)));
            commander.player().doAction(new ActionEquip(commander.player(), pick));
        }
        fillSlots(commander.player());
    }
}
