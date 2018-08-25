package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.things.UThing;
import ure.ui.modals.UModal;

import java.util.ArrayList;

public class WidgetThingList extends Widget {

    public ArrayList<UThing> things;
    public int iconSpacing = 1;
    public int selection;

    public WidgetThingList(UModal modal, int x, int y, int width, int height) {
        super(modal);
        focusable = true;
        setDimensions(x, y, width, height);
    }

    @Override
    public void drawMe() {
        if (things == null) return;
        for (int i=0;i<things.size();i++) {
            UThing thing = things.get(i);
            drawIcon(thing.icon(), 0, i);
            drawString(thing.name(),  1+iconSpacing, i, (i == selection) ? null : UColor.GRAY, (i == selection && focused) ? hiliteColor() : null);
        }
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N")) select(cursorMove(selection, -1, things.size()));
            else if (c.id.equals("MOVE_S")) select(cursorMove(selection, 1, things.size()));
        }
        super.hearCommand(c, k);
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        select(Math.max(0, Math.min(things.size() - 1, mousey)));
    }

    void select(int newselection) {
        selection = newselection;
        modal.widgetChanged(this);
    }

    public UThing thing() {
        if (things == null) return null;
        else if (things.size() == 0) return null;
        return things.get(selection);
    }

    public void setThings(ArrayList<UThing> newthings) {
        things = newthings;
        selection = 0;
        modal.widgetChanged(this);
    }
}
