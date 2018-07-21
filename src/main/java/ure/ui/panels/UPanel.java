package ure.ui.panels;

import ure.math.UColor;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.ui.View;

import javax.inject.Inject;

/**
 * UPanel is a generic panel to embed in the game window, which cah display game status info.
 *
 */
public class UPanel extends View {

    @Inject
    public UCommander commander;

    UColor fgColor, bgColor, borderColor;
    int pixelw, pixelh;
    int padX, padY;

    boolean hidden;

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
}
