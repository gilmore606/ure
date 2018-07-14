package ure.ui.modals;

import ure.math.UColor;

public class UModalNotify extends UModal {

    String text;

    public UModalNotify(String text, UColor bgColor) {
        super(null, "");
        this.text = text;
        setDimensions(text.length(), 1);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }
}
