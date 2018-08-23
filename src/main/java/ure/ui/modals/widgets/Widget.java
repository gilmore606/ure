package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.modals.UModal;

public class Widget {

    UModal modal;
    public int x,y,w,h;
    public boolean focusable = false;
    public boolean focused = false;
    public boolean hidden = false;

    public Widget(UModal modal) {
        this.modal = modal;
    }

    public void setDimensions(int x, int y, int w, int h) {
        this.x=x; this.y=y; this.w = w; this.h=h;
    }

    public void draw() { }
    public void mouseInside(int mousex, int mousey) { }
    public void mouseClick(int mousex, int mousey) { }
    public void mouseRightClick(int mousex, int mousey) { }
    public void hearCommand(UCommand command, GLKey k) { }

    public int gw() { return modal.config.getTileWidth(); }
    public int gh() { return modal.config.getTileHeight(); }
}