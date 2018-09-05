package ure.ui.panels;

import com.google.common.eventbus.EventBus;
import ure.math.UColor;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.sys.UConfig;
import ure.ui.Icons.Icon;
import ure.ui.View;

import javax.inject.Inject;

/**
 * UPanel is a generic panel to embed in the game window, which cah display game status info.
 *
 */
public class UPanel extends View {

    @Inject
    public UCommander commander;
    @Inject
    public UConfig config;
    @Inject
    public EventBus bus;

    UColor fgColor, bgColor, borderColor;
    int pixelw, pixelh;
    int padX, padY;

    public static int XPOS_LEFT = -1;
    public static int XPOS_FIT = 0;
    public static int XPOS_RIGHT = 1;
    public static int YPOS_TOP = -1;
    public static int YPOS_FIT = 0;
    public static int YPOS_BOTTOM = 1;

    public int layoutXpos;
    public int layoutYpos;
    public int layoutWidthMin;
    public float layoutWidthFrac;
    public int layoutWidthMax;
    public int layoutHeightMin;
    public float layoutHeightFrac;
    public int layoutHeightMax;

    public boolean hidden;

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

    public UPanel(int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        Injector.getAppComponent().inject(this);
        padX = _padx;
        padY = _pady;
        fgColor = _fgColor;
        bgColor = (_bgColor == null ? config.getPanelBgColor() : _bgColor);
        borderColor = _borderColor;
        hidden = true;
    }

    public void setLayout(int layoutXpos, int layoutYpos, int layoutWidthMin, float layoutWidthFrac, int layoutWidthMax, int layoutHeightMin, float layoutHeightFrac, int layoutHeightMax) {
        this.layoutXpos = layoutXpos;
        this.layoutYpos = layoutYpos;
        this.layoutWidthMin = layoutWidthMin;
        this.layoutWidthFrac = layoutWidthFrac;
        this.layoutWidthMax = layoutWidthMax;
        this.layoutHeightMin = layoutHeightMin;
        this.layoutHeightFrac = layoutHeightFrac;
        this.layoutHeightMax = layoutHeightMax;
    }

    public int widthForXsize(int xsize) {
        int w = (int)((float)(xsize / config.getTileWidth()) * layoutWidthFrac);
        return Math.max(layoutWidthMin, Math.min(layoutWidthMax, w));
    }
    public int heightForYsize(int ysize) {
        int h = (int)((float)(ysize / config.getTileHeight()) * layoutHeightFrac);
        return Math.max(layoutHeightMin, Math.min(layoutHeightMax, h));
    }

    public void resizeView(int x, int y, int w, int h) {
        setBounds(x,y,w,h);
    }

    public void hide() { hidden = true; }
    public void unHide() { hidden = false; }
    public boolean isHidden() { return hidden; }

    public void setPosition(int posx, int posy) {
        setBounds(posx, posy, width, height);
    }

    public void draw() {
        // TODO : support glyph based frames same as UModal
        if (!hidden) {
            drawFrame();
            drawContent();
        }
    }

    public void drawFrame() {
        if (borderColor != null)
            renderer.drawRectBorder(1, 1, width - 2, height - 2, 3, UColor.CLEAR, borderColor);
        renderer.drawRect(0,0,width,height, bgColor);
    }

    public void drawContent() {

    }

    public void mouseClick() {

    }

    public void mouseRightClick() {

    }

    public void drawString(String string, int x, int y, UColor color) {
        if (string != null) {
            int linex = padX + (x * gw());
            int liney = padY + (y * gw());
            renderer.drawString(linex, liney, color, string);
        }
    }
    public void drawIcon(Icon icon, int x, int y) {
        if (icon != null)
            icon.draw(padX + (x*gw()), padY + (y*gw()));
    }

    public int gw() { return config.getTileWidth(); }
    public int gh() { return config.getTileHeight(); }

    public int mouseX() {
        return (commander.mouseX() - absoluteX()) / gw();
    }
    public int mouseY() {
        return (commander.mouseY() - absoluteY()) / gh();
    }

}
