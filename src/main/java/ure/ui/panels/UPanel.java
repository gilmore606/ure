package ure.ui.panels;

import com.google.common.eventbus.EventBus;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.ui.Icon;
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
    public EventBus bus;

    UColor fgColor, bgColor, borderColor;
    int pixelw, pixelh;
    int padX, padY;

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

    public UPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        Injector.getAppComponent().inject(this);
        pixelw = _pixelw;
        pixelh = _pixelh;
        padX = _padx;
        padY = _pady;
        fgColor = _fgColor;
        bgColor = _bgColor;
        borderColor = _borderColor;
        hidden = true;
    }

    public void hide() { hidden = true; }
    public void unHide() { hidden = false; }
    public boolean isHidden() { return hidden; }

    public void setPosition(int posx, int posy) {
        setBounds(posx, posy, pixelw, pixelh);
    }

    public void draw(URenderer renderer) {
        // TODO : support glyph based frames same as UModal
        if (!hidden)
            renderer.drawRectBorder(1, 1, width - 2, height - 2, 1, bgColor, borderColor);
    }


}
