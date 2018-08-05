package ure.ui.modals;

import ure.math.UColor;
import ure.render.URenderer;

public class UModalNotify extends UModal {

    String text;
    String[] textlines;
    int xpad,ypad;

    public UModalNotify(String text, UColor bgColor, int xpad, int ypad) {
        super(null, "", bgColor);
        this.text = text;
        textlines = splitLines(text);
        this.xpad = xpad;
        this.ypad = ypad;
        int width = longestLine(textlines);
        setDimensions(width + xpad*2, ypad*2 + textlines.length);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawStrings(renderer, textlines, xpad, ypad);
    }

}
