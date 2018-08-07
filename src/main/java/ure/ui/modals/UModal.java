package ure.ui.modals;

import ure.sys.*;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.terrain.UTerrainCzar;
import ure.ui.Icon;
import ure.ui.View;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UModal intercepts player commands and (probably) draws UI in response, and returns a value to
 * a callback when it wants to (i.e. when the user is finished).
 *
 */
public class UModal extends View implements UAnimator {

    @Inject
    public UCommander commander;
    @Inject
    public UTerrainCzar terrainCzar;

    HearModal callback;
    String callbackContext;
    public int cellw = 0;
    public int cellh = 0;
    public int xpos = 0;
    public int ypos = 0;
    public int mousex, mousey;
    public UColor bgColor;
    HashMap<String,TextFrag> texts;
    public boolean dismissed;
    int dismissFrames = 0;
    int dismissFrameEnd = 0;

    class TextFrag {
        String name;
        String text;
        int row;
        int col;
        UColor color;

        public TextFrag(String tname, String ttext, int trow, int tcol, UColor tcolor) {
            name = tname;
            text = ttext;
            row = trow;
            col = tcol;
            color = tcolor;
        }
    }

    public UModal(HearModal _callback, String _callbackContext, UColor _bgColor) {
        Injector.getAppComponent().inject(this);
        callback = _callback;
        callbackContext = _callbackContext;
        if (_bgColor == null)
            bgColor = commander.config.getModalBgColor();
        else
            bgColor = _bgColor;
    }

    public void onOpen() {

    }

    public int gw() { return commander.config.getTileWidth(); }
    public int gh() { return commander.config.getTileHeight(); }

    public void setBgColor(UColor color) {
        bgColor = color;
    }

    public void setDimensions(int x, int y) {
        cellw = x;
        cellh = y;
        int screenw = 0, screenh = 0;
        if (commander.config.getModalPosition() == UConfig.POS_WINDOW_CENTER) {
            screenw = commander.config.getScreenWidth();
            screenh = commander.config.getScreenHeight();
        } else {
            screenw = commander.modalCamera().getWidthInCells() * gw();
            screenh = commander.modalCamera().getHeightInCells() * gh();
        }

        xpos = (screenw - (cellw * gw())) / 2;
        ypos = (screenh - (cellh * gh())) / 2;
    }

    @Override
    public void draw() {
        if (cellw > 0 && cellh > 0) {
            drawFrame();
        }
        drawContent();
    }

    public void drawContent() {
        commander.printScroll("Hit any key to continue...");
    }

    public void drawIcon(Icon icon, int x, int y) {
        icon.draw(x*gw()+xpos,y*gh()+ypos);
    }

    public void drawString(String string, int x, int y) {
        drawString(string,x,y,commander.config.getTextColor(), null);
    }
    public void drawString(String string, int x, int y, UColor color) {
        drawString(string,x,y,color, null);
    }
    public void drawString(String string, int x, int y, UColor color, UColor highlight) {
        if (highlight != null) {
            int stringWidth = renderer.textWidth(string) + 4;
            renderer.drawRect(x * gw() + xpos - 2, y * gh() + ypos - 3,
                    stringWidth, commander.config.getTextHeight() + 4, highlight);
        }
        if (color == null)
            color = commander.config.getTextColor();
        renderer.drawString(x*gw()+xpos,y*gh()+ypos,color,string);
    }
    public void drawTile(char glyph, int x, int y, UColor color) {
        renderer.drawTile(glyph, x*gw()+xpos,y*gh()+ypos,color);
    }

    public void drawFrame() {
        if (commander.config.getModalShadowStyle() == UConfig.SHADOW_BLOCK) {
            UColor shadowColor = commander.config.getModalShadowColor();
            renderer.drawRect(xpos, ypos, relx(cellw+2)-xpos, rely(cellh+2)-ypos, shadowColor);
        }
        UColor color = commander.config.getModalFrameColor();
        int border = commander.config.getModalFrameLine();
        if (border > 0)
            renderer.drawRectBorder(xpos - gw(),ypos - gh(),relx(cellw+2)-xpos,rely(cellh+2)-ypos,border, bgColor, color);
        else
            renderer.drawRect(xpos - gw(), ypos - gh(),  relx(cellw+2) - xpos,rely(cellh+2) - ypos, bgColor);
        String frames = commander.config.getUiFrameGlyphs();

        if (frames != null) {
            renderer.drawTile(frames.charAt(0), relx(-1), rely(-1), color);
            renderer.drawTile(frames.charAt(2), relx(cellw), rely(-1), color);
            renderer.drawTile(frames.charAt(4), relx(cellw), rely(cellh), color);
            renderer.drawTile(frames.charAt(6), relx(-1), rely(cellh), color);
            for (int x = 0;x < cellw;x++) {
                renderer.drawTile(frames.charAt(1), relx(x), rely(-1), color);
                renderer.drawTile(frames.charAt(5), relx(x), rely(cellh), color);
            }
            for (int y = 0;y < cellh;y++) {
                renderer.drawTile(frames.charAt(3), relx(-1), rely(y), color);
                renderer.drawTile(frames.charAt(7), relx(cellw), rely(y), color);
            }
        }

    }

    int textWidthInCells(String string) {
        return renderer.textWidth(string) / gw() + 1;
    }

    /**
     * Convert a modal-relative cell position to an absolute screen position.
     */
    public int relx(int x)  { return (x * commander.config.getTileWidth()) + xpos; }
    public int rely(int y)  { return (y * commander.config.getTileHeight()) + ypos; }

    public void hearCommand(UCommand command, GLKey k) {
        dismiss();
    }

    void dismiss() {
        commander.speaker.playUIsound(commander.config.soundUIselectClose, 1f);
        dismissed = true;
    }

    void escape() {
        dismissed = true;
        dismissFrameEnd = 0;
        commander.speaker.playUIsound(commander.config.soundUIcancelClose, 1f);
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, UColor.COLOR_WHITE));
    }
    public void addText(String name, String text, int row, int col, UColor color) {
        addTextFrag(new TextFrag(name, text, row, col, color));
    }
    void addTextFrag(TextFrag frag) {
        texts.put(frag.name, frag);
    }

    public void animationTick() {
        if (dismissed) {
            dismissFrames++;
            if (dismissFrames > dismissFrameEnd) {
                commander.detachModal(this);
            }
        } else {
            updateMouse();
        }
    }

    void updateMouse() {
        mousex = (commander.mouseX() - xpos) / gw();
        mousey = (commander.mouseY() - ypos) / gh();
    }

    int mouseToSelection(int menusize, int yoffset, int selection) { return mouseToSelection(menusize,yoffset,selection,0,1000); }
    int mouseToSelection(int menusize, int yoffset, int selection, int xmin, int xmax) {
        int mousesel = mousey - yoffset;
        if (mousesel < 0)
            return selection;
        if (mousesel >= menusize)
            return selection;
        if (mousex < xmin || mousey >= xmax)
            return selection;
        return mousesel;
    }
    public void mouseClick() {
        dismiss();
    }
    public void mouseRightClick() {
        escape();
    }

    public String[] splitLines(String text) {
        if (text == null) return null;
        ArrayList<String> linebuf = new ArrayList<>();
        while (text.indexOf("\n") > 0) {
            int split = text.indexOf("\n");
            String broke = text.substring(0,split);
            text = text.substring(split+1);
            linebuf.add(broke);
        }
        linebuf.add(text);
        String[] lines = new String[linebuf.size()];
        int i=0;
        for (String line: linebuf) {
            lines[i] = line;
            i++;
        }
        return lines;
    }

    public int longestLine(String[] lines) {
        int longest = 0;
        for (String line : lines) {
            if (line.length() > longest)
                longest = line.length();
        }
        return longest;
    }

    public void drawStrings(String[] lines, int x, int y) {
        if (lines != null) {
            int i = 0;
            for (String line: lines) {
                drawString(line, x, y+i);
                i++;
            }
        }
    }

    public void showDetail(Entity entity, int xoff, int yoff) {
        if (entity == null) return;
        drawString(entity.getName(), xoff, yoff);
        ArrayList<String> details = entity.UIdetails(callbackContext);
        int linepos = 1;
        for (String line : details) {
            drawString(line, xoff, linepos+yoff, UColor.COLOR_LIGHTGRAY);
            linepos++;
        }
    }

    public int cursorMove(int cursor, int delta, int total) {
        int oldcursor = cursor;
        cursor += delta;
        if (cursor < 0) {
            if (commander.config.isWrapSelect()) {
                cursor = total - 1;
            } else {
                cursor = 0;
            }
        } else if (cursor >= total) {
            if (commander.config.isWrapSelect()) {
                cursor = 0;
            } else {
                cursor = total - 1;
            }
        }
        String sound;
        if (cursor > oldcursor) {
            sound = commander.config.soundUIcursorDown;
        } else if (cursor < oldcursor) {
            sound = commander.config.soundUIcursorUp;
        } else {
            sound = commander.config.soundUIbumpLimit;
        }
        commander.speaker.playUIsound(sound, 0.5f);
        return cursor;
    }
}
