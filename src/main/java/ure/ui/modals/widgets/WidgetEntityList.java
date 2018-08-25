package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.things.UThing;
import ure.ui.modals.UModal;

import java.util.ArrayList;

public class WidgetEntityList extends Widget {

    public ArrayList<Entity> entities;
    public int iconSpacing = 1;
    public int selection;

    public WidgetEntityList(UModal modal, int x, int y, int width, int height) {
        super(modal);
        focusable = true;
        setDimensions(x, y, width, height);
    }

    @Override
    public void drawMe() {
        if (entities == null) return;
        for (int i = 0;i< entities.size();i++) {
            Entity entity = entities.get(i);
            if (entity != null) {
                drawIcon(entity.icon(), 0, i);
                drawString(entity.name(), 1 + iconSpacing, i, (i == selection) ? null : grayColor(), (i == selection && focused) ? hiliteColor() : null);
            } else {
                drawString("        ", 1 + iconSpacing, i, (i == selection) ? null : grayColor(), (i == selection && focused) ? hiliteColor() : null);
            }
        }
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N")) select(cursorMove(selection, -1, entities.size()));
            else if (c.id.equals("MOVE_S")) select(cursorMove(selection, 1, entities.size()));
        }
        super.hearCommand(c, k);
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        select(Math.max(0, Math.min(entities.size() - 1, mousey)));
    }

    void select(int newselection) {
        selection = newselection;
        modal.widgetChanged(this);
    }

    public Entity entity() {
        if (entities == null) return null;
        else if (entities.size() == 0) return null;
        return entities.get(selection);
    }

    public void setEntities(ArrayList<Entity> newentities) {
        entities = newentities;
        selection = 0;
        modal.widgetChanged(this);
    }
    public void setThings(ArrayList<UThing> newentities) {
        entities = new ArrayList<Entity>();
        for (UThing t : newentities)
            entities.add(t);
    }
}
