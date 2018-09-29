package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.View;
import ure.ui.modals.UModal;
import ure.ui.sounds.Sound;

public class Widget extends View {

    public UModal modal;
    public int col, row, cellw, cellh;
    public boolean focusable = false;
    public boolean focused = false;
    public boolean hidden = false;
    public boolean dismissFlash = false;

    public Widget(UModal modal) {
        this.modal = modal;
    }

    public void setDimensions(int col, int row, int cellw, int cellh) {
        this.col = col;
        this.row = row;
        this.cellw = cellw;
        this.cellh = cellh;
        this.x = col*gw();
        this.y = row*gh();
        this.width = cellw*gw();
        this.height = cellh*gh();
    }

    public void move(int col, int row) {
        setDimensions(col, row, cellw, cellh);
    }

    public void loseFocus() {
        focused = false;
    }

    public void gainFocus() {
        focused = true;
    }

    public void pressWidget() {

    }

    public void draw() {
        if (!hidden && modal.drawWidgets)
            drawMe();
    }

    public void drawMe() {

    }

    public void animationTick() {

    }

    public void mouseInside(int mousex, int mousey) { }
    public void mouseClick(int mousex, int mousey) { }
    public void mouseRightClick(int mousex, int mousey) { }
    public void hearCommand(UCommand command, GLKey k) { }

    public int gw() { return modal.config.getTileWidth(); }
    public int gh() { return modal.config.getTileHeight(); }

    public int mousePixelX() {
        return (modal.commander.mouseX() - absoluteX());
    }
    public int mousePixelY() {
        return (modal.commander.mouseY() - absoluteY());
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

    public void drawIcon(Icon icon, int col, int row) {
        icon.draw(col*gw(),row*gh());
    }

    public void drawString(String string, int col, int row) {
        drawString(string,col,row,modal.config.getTextColor(), null);
    }
    public void drawString(String string, int col, int row, UColor color) {
        drawString(string,col,row,color, null);
    }
    public void drawString(String string, int col, int row, UColor color, UColor highlight) {
        if (highlight != null) {
            int stringWidth = modal.renderer.textWidth(string) + 4;
            modal.renderer.drawRect(col*gw() - 4, row*gh() - 3,
                    stringWidth+4, modal.config.getTextHeight() + 5, highlight);
        }
        if (color == null)
            color = modal.config.getTextColor();
        modal.renderer.drawString(col*gw(),row*gh(),color,string);
    }
    public void drawTile(char glyph, int col, int row, UColor color) {
        modal.renderer.drawTile(glyph, col*gw(),row*gh(),color);
    }
    public UColor hiliteColor() {
        if (modal.dismissed) {
            if ((modal.dismissFrames / 2) % 2 == 0) {
                return UColor.CLEAR;
            } else {
                return modal.config.getHiliteColor();
            }
        } else {
            return modal.config.getHiliteColor();
        }
    }
    public UColor grayColor() {
        return modal.config.getTextGray();
    }
}