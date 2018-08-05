package ure.ui.modals;

import ure.actors.Bodypart;
import ure.actors.UActor;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.things.UThing;

import java.util.ArrayList;

public class UModalEquipment extends UModal {

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
        int height = 0;
        slotsBodyparts = new ArrayList<>();
        slotsThings = new ArrayList<>();
        for (Bodypart bodypart : actor.getBody().getParts()) {
            for (int i=0;i<bodypart.slots;i++) {
                height += 1;
                slotsBodyparts.add(bodypart);
                slotsThings.add(null);
            }
        }
        for (UThing thing : actor.equipment()) {
            for (int i=0;i<slotsThings.size();i++) {
                if (slotsBodyparts.get(i).name.equals(thing.equipSlots[0]) && slotsThings.get(i) == null) {
                    slotsThings.set(i,thing);
                }
            }
        }
        width = 26;
        setDimensions(width+xpad,height+ypad);
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
        if (thing != null) {
            drawString(renderer, thing.getName(), detailX+xpad, 0+ypad);
            ArrayList<String>details = thing.UIdetails("");
            int lineY=2;
            for (String line : details) {
                drawString(renderer, line, detailX+xpad, lineY+ypad);
                lineY++;
            }
        }
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

    }
}
