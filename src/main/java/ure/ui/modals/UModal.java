package ure.ui.modals;

import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.sys.UCommander;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.UConfig;
import ure.ui.Icon;
import ure.ui.UCamera;
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
    UCommander commander;

    HearModal callback;
    String callbackContext;
    public int cellw = 0;
    public int cellh = 0;
    public int xpos = 0;
    public int ypos = 0;
    public UColor bgColor;
    HashMap<String,TextFrag> texts;

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

    public int gw() { return commander.config.getGlyphWidth(); }
    public int gh() { return commander.config.getGlyphHeight(); }

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
    public void draw(URenderer renderer) {
        if (cellw > 0 && cellh > 0) {
            drawFrame(renderer);
        }
        drawContent(renderer);
    }

    public void drawContent(URenderer renderer) {
        commander.printScroll("Hit any key to continue...");
        //for (String textName : texts.keySet()) {
            //TextFrag frag = texts.get(textName);
            // TODO: Fix for new renderer
            //g.setFont(renderer.font);
            //g.setColor(frag.color);
            //g.drawString(frag.text, frag.row * renderer.cellWidth(), ((frag.col + 1) * renderer.cellHeight()) + 0);
        //}
    }

    public void drawIcon(URenderer renderer, Icon icon, int x, int y) {
        icon.draw(renderer,x*gw()+xpos,y*gh()+ypos);
    }

    public void drawString(URenderer renderer, String string, int x, int y) {
        renderer.drawString(x*gw()+xpos,y*gh()+ypos, commander.config.getTextColor(), string);
    }

    public void drawFrame(URenderer renderer) {
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
            renderer.drawGlyph(frames.charAt(0), relx(-1), rely(-1), color, 0, 0);
            renderer.drawGlyph(frames.charAt(2), relx(cellw), rely(-1), color, 0, 0);
            renderer.drawGlyph(frames.charAt(4), relx(cellw), rely(cellh), color, 0, 0);
            renderer.drawGlyph(frames.charAt(6), relx(-1), rely(cellh), color, 0, 0);
            for (int x = 0;x < cellw;x++) {
                renderer.drawGlyph(frames.charAt(1), relx(x), rely(-1), color, 0, 0);
                renderer.drawGlyph(frames.charAt(5), relx(x), rely(cellh), color, 0, 0);
            }
            for (int y = 0;y < cellh;y++) {
                renderer.drawGlyph(frames.charAt(3), relx(-1), rely(y), color, 0, 0);
                renderer.drawGlyph(frames.charAt(7), relx(cellw), rely(y), color, 0, 0);
            }
        }

    }

    /**
     * Convert a modal-relative cell position to an absolute screen position.
     */
    public int relx(int x)  { return (x * commander.config.getGlyphWidth()) + xpos; }
    public int rely(int y)  { return (y * commander.config.getGlyphHeight()) + ypos; }

    public void hearCommand(UCommand command, Character c) {
        dismiss();
    }

    void dismiss() {
        commander.detachModal();
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
}
