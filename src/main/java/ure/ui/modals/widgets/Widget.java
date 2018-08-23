package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.View;
import ure.ui.modals.UModal;
import ure.ui.sounds.Sound;

public class Widget extends View {

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

    public void loseFocus() {
        focused = false;
    }

    public void gainFocus() {
        focused = true;
    }

    public void draw() {
        if (!hidden && modal.drawWidgets)
            drawMe();
    }

    public void drawMe() {

    }

    public void mouseInside(int mousex, int mousey) { }
    public void mouseClick(int mousex, int mousey) { }
    public void mouseRightClick(int mousex, int mousey) { }
    public void hearCommand(UCommand command, GLKey k) { }

    public int gw() { return modal.config.getTileWidth(); }
    public int gh() { return modal.config.getTileHeight(); }

    public int mousePixelX() {
        return (modal.commander.mouseX() - modal.xpos) - x*gw();
    }
    public int mousePixelY() {
        return (modal.commander.mouseY() - modal.ypos) - y*gh();
    }
    public int pixelX() {
        return modal.xpos + x*gw();
    }
    public int pixelY() {
        return modal.ypos + y*gh();
    }

    public int cursorMove(int cursor, int delta, int total) {
        int oldcursor = cursor;
        cursor += delta;
        if (cursor < 0) {
            if (modal.config.isWrapSelect()) {
                cursor = total - 1;
            } else {
                cursor = 0;
            }
        } else if (cursor >= total) {
            if (modal.config.isWrapSelect()) {
                cursor = 0;
            } else {
                cursor = total - 1;
            }
        }
        Sound sound;
        if (cursor > oldcursor) {
            sound = modal.config.soundCursorDown;
        } else if (cursor < oldcursor) {
            sound = modal.config.soundCursorUp;
        } else {
            sound = modal.config.soundBumpLimit;
        }
        modal.speaker.playUI(sound);
        return cursor;
    }

    public void drawIcon(Icon icon, int x, int y) {
        icon.draw(pixelX() + x*gw(),pixelY() + y*gh());
    }

    public void drawString(String string, int x, int y) {
        drawString(string,x,y,modal.config.getTextColor(), null);
    }
    public void drawString(String string, int x, int y, UColor color) {
        drawString(string,x,y,color, null);
    }
    public void drawString(String string, int x, int y, UColor color, UColor highlight) {
        if (highlight != null) {
            int stringWidth = modal.renderer.textWidth(string) + 4;
            modal.renderer.drawRect(x * gw() + pixelX() - 2, y * gh() + pixelY() - 3,
                    stringWidth, modal.config.getTextHeight() + 4, highlight);
        }
        if (color == null)
            color = modal.config.getTextColor();
        modal.renderer.drawString(x*gw()+pixelX(),y*gh()+pixelY(),color,string);
    }
    public void drawTile(char glyph, int x, int y, UColor color) {
        modal.renderer.drawTile(glyph, x*gw()+pixelX(),y*gh()+pixelY(),color);
    }
}