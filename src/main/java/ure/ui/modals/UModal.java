package ure.ui.modals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import ure.actors.UActorCzar;
import ure.sys.*;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.events.ResolutionChangedEvent;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;
import ure.ui.Icons.Icon;
import ure.ui.Icons.UIconCzar;
import ure.ui.modals.widgets.Widget;
import ure.ui.sounds.Sound;
import ure.ui.sounds.USpeaker;
import ure.ui.View;

import javax.inject.Inject;
import java.util.ArrayList;

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
    @Inject
    EventBus bus;

    public HearModal callback;
    public String callbackContext;
    public int cellw = 0;
    public int cellh = 0;
    public int colpad = 0;
    public int rowpad = 0;
    public int mousex, mousey;
    public UColor bgColor, shadowColor, frameColor;
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
        bus.register(this);
        callback = _callback;
        callbackContext = _callbackContext;
        bgColor = new UColor(config.getModalBgColor());
        shadowColor = new UColor(config.getModalShadowColor());
        frameColor = new UColor(config.getModalFrameColor());
        widgets = new ArrayList<>();
        widgetsFocusable = new ArrayList<>();
    }

    public void onOpen() {
        if (!isChild()) {
            zoomFrame = 0;
            zoom = 0.2f;
            zoomDir = 1;
        }
    }

    public int gw() { return config.getTileWidth(); }
    public int gh() { return config.getTileHeight(); }

    public void setBgColor(UColor color) {
        bgColor = color;
    }

    public void setDimensions(int cols, int rows) {
        cellw = cols;
        cellh = rows;
        int screenw = 0, screenh = 0;
        if (config.getModalPosition() == UConfig.POS_WINDOW_CENTER) {
            screenw = renderer.getScreenWidth();
            screenh = renderer.getScreenHeight();
        } else {
            screenw = commander.modalCamera().getWidthInCells() * gw();
            screenh = commander.modalCamera().getHeightInCells() * gh();
        }
        width = cellw * gw();
        height = cellh * gh();
        x = (screenw / 2 - (width / 2));
        y = (screenh / 2 - (height / 2));
        if (config.getModalPosition() == UConfig.POS_CAMERA_CENTER) {
            x += commander.modalCamera().getX();
            y += commander.modalCamera().getY();
        }
    }

    public void sizeToWidgets() {
        int dimx = 0;
        int dimy = 0;
        for (Widget widget : widgets) {
            dimx = Math.max(dimx, widget.col + widget.cellw);
            dimy = Math.max(dimy, widget.row + widget.cellh);
        }
        setDimensions(dimx,dimy);
    }

    public void setChildPosition(int col, int row, UModal parent) {
        x = col*gw() + parent.x;
        y = row*gh() + parent.y;
    }

    @Subscribe
    public void resolutionChanged(ResolutionChangedEvent event) {
        setDimensions(cellw, cellh);
    }

    public void setTitle(String s) { title = s; }

    public void setPad(int colpad, int rowpad) {
        this.colpad = colpad;
        this.rowpad = rowpad;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        addChild(widget);
        if (widget.focusable) {
            widgetsFocusable.add(widget);
            if (focusWidget == null) {
                focusToWidget(widget);
            }
        }
    }
    public void removeWidget(Widget widget) {
        widgets.remove(widget);
        removeChild(widget);
        if (focusWidget == widget)
            focusWidget = null;
    }

    public void addCenteredWidget(Widget widget) {
        centerWidget(widget);
        addWidget(widget);
    }
    public void centerWidget(Widget widget) {
        widget.col = (cellw / 2 - (widget.cellw / 2));
        widget.setDimensions(widget.col, widget.row, widget.cellw, widget.cellh);
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

    public void drawIcon(Icon icon, int col, int row) {
        icon.draw(col*gw(),row*gh());
    }

    public void drawString(String string, int col, int row) {
        drawString(string,col,row,config.getTextColor(), null);
    }
    public void drawString(String string, int col, int row, UColor color) {
        drawString(string,col,row,color, null);
    }
    public void drawString(String string, int col, int row, UColor color, UColor highlight) {
        if (highlight != null) {
            int stringWidth = renderer.textWidth(string) + 4;
            renderer.drawRect(col * gw() - 2, row * gh() - 3,
                    stringWidth, config.getTextHeight() + 4, highlight);
        }
        if (color == null)
            color = config.getTextColor();
        renderer.drawString(col*gw(),row*gh(),color,string);
    }
    public void drawTile(char glyph, int col, int row, UColor color) {
        renderer.drawTile(glyph, col*gw(),row*gh(),color);
    }

    public void drawFrame() {
        bgColor.setAlpha(zoom);
        shadowColor.setAlpha(zoom/2);
        frameColor.setAlpha(zoom);
        float shadowOffset = isChild() ? 0.5f : 1f;
        int _cellw = (int)(zoom * (float)(cellw + colpad *2));
        int _cellh = (int)(zoom * (float)(cellh + rowpad *2));
        int _xpos = (((cellw + colpad * 2)/2 - _cellw / 2) - colpad)*gw();
        int _ypos = (((cellh + rowpad * 2)/2 - _cellh / 2) - rowpad)*gh();
        if (config.getModalShadowStyle() == UConfig.SHADOW_BLOCK) {
            renderer.drawRect(_xpos - gw() + ((int)(gw()*shadowOffset)), _ypos - gh() + ((int)(gh()*shadowOffset)), (_cellw+2)*gw(), (_cellh+2)*gh(), shadowColor);
        }
        int border = config.getModalFrameLine();
        if (border > 0)
            renderer.drawRectBorder(_xpos - gw(),_ypos - gh(),(_cellw+2)*gw(),(_cellh+2)*gh(),border, bgColor, frameColor);
        else
            renderer.drawRect(_xpos - gw(), _ypos - gh(),  (_cellw+2)*gw(),(_cellh+2)*gh(), bgColor);

        if (title != null && zoom >= 1f) {
            renderer.drawRect(_xpos+gw()-5, _ypos-(int)(gh()*1.5f+3), gw()*textWidth(title)+8,gh()+6,isOnTop() ? config.getTextGray() : UColor.DARKGRAY);
            renderer.drawString(_xpos+gw(),_ypos-(int)(gh()*1.5f), bgColor, title);
        }
    }

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
        speaker.playUI(config.soundCursorDown);
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
        speaker.playUI(config.soundCursorUp);
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
                zoom += (0.8f / config.getModalZoomFrames());
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
        mousex = (commander.mouseX() - absoluteX()) / gw();
        mousey = (commander.mouseY() - absoluteY()) / gh();
        for (Widget widget : widgets) {
            if (mousex >= widget.col && mousey >= widget.row && mousex < widget.col + widget.cellw && mousey < widget.row + widget.cellh) {
                mouseInside(widget, mousex, mousey);
            }
        }
    }

    public void mouseInside(Widget widget, int mousex, int mousey) {
        widget.mouseInside(mousex - widget.col, mousey - widget.row);
        if (widget.focusable && focusWidget != widget) {
            focusToWidget(widget);
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
            if (mousex >= widget.col && mousey >= widget.row && mousex < widget.col + widget.cellw && mousey < widget.row + widget.cellh) {
                widget.mouseClick(mousex - widget.col, mousey - widget.row);
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
            if (mousex >= widget.col && mousey >= widget.row && mousex < widget.col + widget.cellw && mousey < widget.row + widget.cellh) {
                widget.mouseRightClick(mousex - widget.col, mousey - widget.row);
            }
        }
    }

    public void pressWidget(Widget widget) {
        widget.pressWidget();
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
        return commander.isChildModal(this);
    }
    public boolean isOnTop() {
        return commander.modal() == this;
    }
}
