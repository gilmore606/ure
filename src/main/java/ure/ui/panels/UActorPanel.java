package ure.ui.panels;

import ure.actors.UActor;
import ure.math.UColor;

import java.util.ArrayList;

public class UActorPanel extends UPanel {

    ArrayList<UActor> actors;

    public UActorPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
    }

    public void updateActors() {

    }
}
