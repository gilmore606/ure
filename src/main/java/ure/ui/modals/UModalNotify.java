package ure.ui.modals;

import ure.math.UColor;
import ure.render.URenderer;

public class UModalNotify extends UModal {

    String text;
    int xpad,ypad;

    public UModalNotify(String text, UColor bgColor, int xpad, int ypad) {
        super(null, "");
        this.text = text;
        this.xpad = xpad;
        this.ypad = ypad;
        setDimensions(text.length() + xpad, ypad + 1);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }

    @Override
    public void drawContent(URenderer renderer) {
        renderer.drawString(relx(xpad/2), rely(ypad/2), commander.config.getTextColor(), text);
    }
}
