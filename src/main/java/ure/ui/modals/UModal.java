package ure.ui.modals;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.actors.UActorCzar;
import ure.sys.*;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;
import ure.ui.Icons.Icon;
import ure.ui.Icons.UIconCzar;
import ure.ui.RexFile;
import ure.ui.modals.widgets.Widget;
import ure.ui.sounds.Sound;
import ure.ui.sounds.USpeaker;
import ure.ui.View;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

/**
 * UModal intercepts player commands and (probably) draws UI in response, and returns a value to
 * a callback when it wants to (i.e. when the user is finished).
 *
 */
public class UModal extends View implements UAnimator {

    @Inject
    public UCommander commander;
    @Inject
    public UConfig config;
    @Inject
    public USpeaker speaker;
    @Inject
    public UTerrainCzar terrainCzar;
    @Inject
    public UThingCzar thingCzar;
    @Inject
    public UActorCzar actorCzar;
    @Inject
    public UIconCzar iconCzar;
    @Inject
    public ObjectMapper objectMapper;

    public HearModal callback;
    public String callbackContext;
    public int cellw = 0;
    public int cellh = 0;
    public int xpad = 0;
    public int ypad = 0;
    public int xpos = 0;
    public int ypos = 0;
    public int mousex, mousey;
    public UColor bgColor;
    public boolean escapable = true;
    public boolean escapeOnRightClick = true;
    public boolean dismissed = false;
    public boolean drawWidgets = false;
    public int dismissFrames = 0;
    int dismissFrameEnd = 9;
    int zoomFrame = 0;
    int zoomDir = 0;
    float zoom = 1f;
    String title;
    ArrayList<Widget> widgets;
    Widget focusWidget;
    ArrayList<Widget> widgetsFocusable;

    public UModal(HearModal _callback, String _callbackContext) {
        Injector.getAppComponent().inject(this);
        callback = _callback;
        callbackContext = _callbackContext;
        bgColor = config.getModalBgColor();
        widgets = new ArrayList<>();
        widgetsFocusable = new ArrayList<>();
    }

    public void onOpen() {
        if (!isChild()) {
            zoomFrame = 0;
            zoom = 0.5f;
            zoomDir = 1;
        }
    }

    public int gw() { return config.getTileWidth(); }
    public int gh() { return config.getTileHeight(); }

    public void setBgColor(UColor color) {
        bgColor = color;
    }

    public void setDimensions(int x, int y) {
        cellw = x;
        cellh = y;
        int screenw = 0, screenh = 0;
        if (config.getModalPosition() == UConfig.POS_WINDOW_CENTER) {
            screenw = config.getScreenWidth();
            screenh = config.getScreenHeight();
        } else {
            screenw = commander.modalCamera().getWidthInCells() * gw();
            screenh = commander.modalCamera().getHeightInCells() * gh();
        }

        xpos = (screenw - (cellw * gw())) / 2;
        ypos = (screenh - (cellh * gh())) / 2;
    }

    public void sizeToWidgets() {
        int dimx = 0;
        int dimy = 0;
        for (Widget widget : widgets) {
            dimx = Math.max(dimx, widget.x + widget.w);
            dimy = Math.max(dimy, widget.y + widget.h);
        }
        setDimensions(dimx,dimy);
    }
    public void setChildPosition(int x, int y, UModal parent) {
        xpos = x*gw() + parent.xpos;
        ypos = y*gh() + parent.ypos;
    }

    public void setTitle(String s) { title = s; }

    public void setPad(int x, int y) {
        xpad = x;
        ypad = y;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        addChild(widget);
        if (focusWidget == null && widget.focusable) {
            focusWidget = widget;
            widget.focused = true;
        }
        if (widget.focusable)
            widgetsFocusable.add(widget);
    }
    public void addCenteredWidget(Widget widget) {
        centerWidget(widget);
        addWidget(widget);
    }
    public void centerWidget(Widget widget) {
        widget.x = (cellw / 2 - (widget.w / 2));
    }

    @Override
    public void draw() {
        if (cellw > 0 && cellh > 0) {
            drawFrame();
        }
        if (zoom >= 1f) {
            drawWidgets = true;
            drawContent();
        } else {
            drawWidgets = false;
        }
    }

    public void drawContent() {
        ;
    }

    public void drawIcon(Icon icon, int x, int y) {
        icon.draw(x*gw()+xpos,y*gh()+ypos);
    }

    public void drawString(String string, int x, int y) {
        drawString(string,x,y,config.getTextColor(), null);
    }
    public void drawString(String string, int x, int y, UColor color) {
        drawString(string,x,y,color, null);
    }
    public void drawString(String string, int x, int y, UColor color, UColor highlight) {
        if (highlight != null) {
            int stringWidth = renderer.textWidth(string) + 4;
            renderer.drawRect(x * gw() + xpos - 2, y * gh() + ypos - 3,
                    stringWidth, config.getTextHeight() + 4, highlight);
        }
        if (color == null)
            color = config.getTextColor();
        renderer.drawString(x*gw()+xpos,y*gh()+ypos,color,string);
    }
    public void drawTile(char glyph, int x, int y, UColor color) {
        renderer.drawTile(glyph, x*gw()+xpos,y*gh()+ypos,color);
    }

    public void drawFrame() {
        int _cellw = (int)(zoom * (float)(cellw + xpad*2));
        int _cellh = (int)(zoom * (float)(cellh + ypad*2));
        int _xpos = xpos + 2 * (int)(0.5f * (cellw - _cellw)*gw());
        _xpos -= xpad;
        int _ypos = ypos + 2 * (int)(0.5f * (cellh - _cellh)*gh());
        _ypos -= ypad;
        if (config.getModalShadowStyle() == UConfig.SHADOW_BLOCK) {
            UColor shadowColor = config.getModalShadowColor();
            renderer.drawRect(_xpos, _ypos, relx(_cellw+2)-_xpos, rely(_cellh+2)-_ypos, shadowColor);
        }
        UColor color = config.getModalFrameColor();
        int border = config.getModalFrameLine();
        if (border > 0)
            renderer.drawRectBorder(_xpos - gw(),_ypos - gh(),relx(_cellw+2)-_xpos,rely(_cellh+2)-_ypos,border, bgColor, color);
        else
            renderer.drawRect(_xpos - gw(), _ypos - gh(),  relx(_cellw+2) - _xpos,rely(_cellh+2) - _ypos, bgColor);
        String frames = config.getUiFrameGlyphs();

        if (frames != null) {
            renderer.drawTile(frames.charAt(0), relx(-1), rely(-1), color);
            renderer.drawTile(frames.charAt(2), relx(_cellw), rely(-1), color);
            renderer.drawTile(frames.charAt(4), relx(_cellw), rely(_cellh), color);
            renderer.drawTile(frames.charAt(6), relx(-1), rely(_cellh), color);
            for (int x = 0;x < _cellw;x++) {
                renderer.drawTile(frames.charAt(1), relx(x), rely(-1), color);
                renderer.drawTile(frames.charAt(5), relx(x), rely(_cellh), color);
            }
            for (int y = 0;y < _cellh;y++) {
                renderer.drawTile(frames.charAt(3), relx(-1), rely(y), color);
                renderer.drawTile(frames.charAt(7), relx(_cellw), rely(y), color);
            }
        }
        if (title != null && zoom >= 1f) {
            renderer.drawRect(_xpos+gw()-5, _ypos-(int)(gh()*1.5f+3), gw()*textWidth(title)+8,gh()+6,config.getModalFrameColor());
            renderer.drawString(_xpos+gw(),_ypos-(int)(gh()*1.5f), bgColor, title);
        }

    }

    /**
     * Convert a modal-relative cell position to an absolute screen position.
     */
    public int relx(int x)  { return (x * config.getTileWidth()) + xpos; }
    public int rely(int y)  { return (y * config.getTileHeight()) + ypos; }

    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("ESC") && escapable) {
                escape();
                return;
            } else if (command.id.equals("PASS")) {
                pressWidget(focusWidget);
            }
        }
        if (k.k == GLFW_KEY_TAB) {
            if (k.shift)
                focusPreviousWidget();
            else
                focusNextWidget();
            return;
        }
        if (focusWidget != null) {
            focusWidget.hearCommand(command, k);
        }
    }

    void focusNextWidget() {
        if (widgetsFocusable.size() == 0) return;
        if (widgetsFocusable.size() == 1) focusToWidget(widgetsFocusable.get(0));
        int focusi = -1;
        for (int i=0;i<widgetsFocusable.size();i++) {
            if (focusWidget == widgetsFocusable.get(i))
                focusi = i;
        }
        focusi++;
        if (focusi >= widgetsFocusable.size()) {
            if (config.isWrapSelect()) focusi = 0;
            else focusi = widgetsFocusable.size() - 1;
        }
        focusToWidget(widgetsFocusable.get(focusi));
    }

    void focusPreviousWidget() {
        if (widgetsFocusable.size() == 0) return;
        if (widgetsFocusable.size() == 1) focusToWidget(widgetsFocusable.get(0));
        int focusi = -1;
        for (int i=0;i<widgetsFocusable.size();i++) {
            if (focusWidget == widgetsFocusable.get(i))
                focusi = i;
        }
        focusi--;
        if (focusi < 0) {
            if (config.isWrapSelect()) focusi = widgetsFocusable.size() - 1;
            else focusi = 0;
        }
        focusToWidget(widgetsFocusable.get(focusi));
    }

    void focusToWidget(Widget widget) {
        if (focusWidget != null) focusWidget.loseFocus();
        focusWidget = widget;
        focusWidget.gainFocus();
    }

    void destroyWidgets() {
        for (Widget widget : widgets) {
            removeChild(widget);
        }
    }

    public void dismiss() {
        speaker.playUI(config.soundSelect);
        dismissed = true;
    }

    public void escape() {
        destroyWidgets();
        dismissed = true;
        dismissFrameEnd = 0;
        speaker.playUI(config.soundCancel);
    }

    public void animationTick() {
        if (dismissed) {
            dismissFrames++;
            if (dismissFrames > dismissFrameEnd) {
                destroyWidgets();
                commander.detachModal(this);
            }
        } else {
            updateMouse();
            if (zoomDir != 0) {
                zoomFrame++;
                zoom += (0.5f / config.getModalZoomFrames());
                if (zoomFrame == config.getModalZoomFrames()) {
                    zoomDir = 0;
                    zoom = 1f;
                }
            }
            for (Widget w : widgets)
                w.animationTick();
        }
    }

    void updateMouse() {
        mousex = (commander.mouseX() - xpos) / gw();
        mousey = (commander.mouseY() - ypos) / gh();
        for (Widget widget : widgets) {
            if (mousex >= widget.x && mousey >= widget.y && mousex < widget.x + widget.w && mousey < widget.y + widget.h) {
                widget.mouseInside(mousex - widget.x, mousey - widget.y);
                if (widget.focusable) {
                    focusToWidget(widget);
                }
            }
        }
    }

    boolean isMouseInside() {
        return (mousex >= 0 && mousey >=0 && mousex < cellw && mousey < cellh);
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
        if (mousesel < selection)
            speaker.playUI(config.soundCursorUp);
        if (mousesel > selection)
            speaker.playUI(config.soundCursorDown);
        return mousesel;
    }
    public void mouseClick() {
        for (Widget widget : widgets) {
            if (mousex >= widget.x && mousey >= widget.y && mousex < widget.x + widget.w && mousey < widget.y + widget.h) {
                widget.mouseClick(mousex - widget.x, mousey - widget.y);
                pressWidget(widget);
            }
        }
    }
    public void mouseRightClick() {
        if (escapable && escapeOnRightClick) {
            escape();
            return;
        }
        for (Widget widget : widgets) {
            if (mousex >= widget.x && mousey >= widget.y && mousex < widget.x + widget.w && mousey < widget.y + widget.h) {
                widget.mouseRightClick(mousex - widget.x, mousey - widget.y);
            }
        }
    }

    public void pressWidget(Widget widget) {

    }

    public void widgetChanged(Widget widget) {

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

    /**
     * Return the length in glyph cells of the longest line of text.
     */
    public int longestLine(String[] lines) {
        int longest = 0;
        for (String line : lines) {
            int len = renderer.textWidth(line);
            if (len > longest) longest = len;
        }
        return longest / gw() + 1;
    }

    /**
     * Return the length in glyph cells of this line of text.
     */
    public int textWidth(String line) {
        return renderer.textWidth(line) / gw() + 1;
    }

    public void drawStrings(String[] lines, int x, int y) { drawStrings(lines,x,y,config.getTextColor()); }
    public void drawStrings(String[] lines, int x, int y, UColor c) {
        if (lines != null) {
            int i = 0;
            for (String line: lines) {
                drawString(line, x, y+i, c);
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
            drawString(line, xoff, linepos+yoff, UColor.LIGHTGRAY);
            linepos++;
        }
    }

    public int cursorMove(int cursor, int delta, int total) {
        int oldcursor = cursor;
        cursor += delta;
        if (cursor < 0) {
            if (config.isWrapSelect()) {
                cursor = total - 1;
            } else {
                cursor = 0;
            }
        } else if (cursor >= total) {
            if (config.isWrapSelect()) {
                cursor = 0;
            } else {
                cursor = total - 1;
            }
        }
        Sound sound;
        if (cursor > oldcursor) {
            sound = config.soundCursorDown;
        } else if (cursor < oldcursor) {
            sound = config.soundCursorUp;
        } else {
            sound = config.soundBumpLimit;
        }
        speaker.playUI(sound);
        return cursor;
    }

    public boolean isChild() {
        return commander.hasChildModal();
    }
}
