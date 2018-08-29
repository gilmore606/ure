package ure.editors.vaulted;

import ure.math.UColor;
import ure.sys.Entity;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;

public class WidgetEntityPalette extends Widget {

    public Entity[][] entities;
    int addCursorX = 0;
    int addCursorY = 0;
    int cursorx = 0;
    int cursory = 0;

    public WidgetEntityPalette(UModal modal, int x, int y, int w, int h) {
        super(modal);
        setDimensions(x,y,w,h);
        focusable = true;
        entities = new Entity[w][h];
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                entities[i][j] = null;
            }
        }
    }

    public Entity entity() {
        return entities[cursorx][cursory];
    }

    @Override
    public void drawMe() {
        for (int x=0;x<cellw;x++) {
            for (int y=0;y<cellh;y++) {
                Entity e = entities[x][y];
                if (e != null) {
                    drawIcon(e.icon(), x, y);
                }
            }
        }
        modal.renderer.drawRectBorder(cursorx*gw()-2,cursory*gh()-2, gw()+4,gh()+4, focused ? 4 : 2, UColor.CLEAR, UColor.YELLOW);
    }

    @Override
    public void mouseClick(int x, int y) {
        if (entities[x][y] != null) {
            cursorx = x;
            cursory = y;
            modal.widgetChanged(this);
        }
    }

    public void add(Entity e) {
        set(addCursorX, addCursorY, e);
        addCursorX++;
        if (addCursorX >= cellw) {
            addCursorX = 0;
            addCursorY++;
            if (addCursorY >= cellh) {
                addCursorY = 0;
            }
        }
    }

    public void set(int x, int y, Entity e) {
        entities[x][y] = e;
    }
}
