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
    public int pageOffset;
    boolean upArrow,downArrow;
    public boolean scrollable = true;

    public WidgetEntityList(UModal modal, int x, int y, int width, int height) {
        super(modal);
        focusable = true;
        pageOffset = 0;
        setClipsToBounds(true);
        setDimensions(x, y, width, height);
    }

    @Override
    public void drawMe() {
        upArrow = downArrow = false;
        if (entities == null) return;
        if (pageOffset > 0) {
            drawTile('^', cellw / 2, 0, hiliteColor());
            upArrow = true;
        }
        for (int i = 0;i<cellh-(scrollable ? 2 : 0);i++) {
            int reali = i+pageOffset;
            if (reali < entities.size() && reali >= 0) {
                Entity entity = entities.get(reali);
                if (entity != null) {
                    drawIcon(entity.icon(), 0, i+(scrollable?1:0));
                    drawString(entity.name(), 1 + iconSpacing, i+(scrollable?1:0), (reali == selection) ? null : grayColor(), (reali == selection && focused) ? hiliteColor() : null);
                } else {
                    drawString("        ", 1 + iconSpacing, i+(scrollable?1:0), (reali == selection) ? null : grayColor(), (reali == selection && focused) ? hiliteColor() : null);
                }
            }
        }
        if (scrollable && ((cellh-2 < entities.size() - pageOffset))) {
            drawTile('v', cellw/2, cellh-1, hiliteColor());
            downArrow = true;
        }
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N"))  {
                select(cursorMove(selection, -1, entities.size()));
            }  else if (c.id.equals("MOVE_S")) {
                select(cursorMove(selection, 1, entities.size()));
            }
            int bottom = (selection - pageOffset) - (cellh - 3);
            if (bottom > 0)
                pageOffset += bottom;
            int top = pageOffset - selection;
            if (top > 0)
                pageOffset -= top;
        }
        super.hearCommand(c, k);
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        if (mousey == 0 && upArrow)
            pageOffset -= 1;
        else if (mousey == cellh - 1 && downArrow)
            pageOffset += 1;
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        if (entities != null) {
            if (mousey > 0 - (scrollable?0:1) && mousey < cellh - (scrollable?1:0)) {
                select(Math.max(0, Math.min(entities.size() - 1, mousey + pageOffset - (scrollable?1:0))));
            } else {
                select(-1);
            }
        }
    }

    void select(int newselection) {
        selection = newselection;
        boundsCheck();
        modal.widgetChanged(this);
    }

    public Entity entity() {
        if (entities == null) return null;
        else if (entities.size() == 0) return null;
        else if (selection < 0) return null;
        return entities.get(selection);
    }

    public void setEntities(ArrayList<Entity> newentities) {
        entities = newentities;
        selection = 0;
        boundsCheck();
        modal.widgetChanged(this);
    }
    public void setThings(ArrayList<UThing> newentities) {
        entities = new ArrayList<Entity>();
        for (UThing t : newentities)
            entities.add(t);
        boundsCheck();
    }

    void boundsCheck() {
        if (selection > entities.size() - 1)
            selection = entities.size() - 1;
    }

    public ArrayList<UThing> getThings() {
        ArrayList<UThing> things = new ArrayList<>();
        for (Entity t : entities) {
            things.add((UThing)t);
        }
        return things;
    }
}
