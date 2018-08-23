package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.ui.modals.UModal;

public class WidgetHSlider extends Widget {
    int value, valuemax;
    int length;
    public WidgetHSlider(UModal modal, int x, int y, int length, int value, int valuemax, boolean showNumber) {
        super(modal);
        setDimensions(x,y,length + (showNumber ? 3 : 0),1);
        focusable = true;
        this.value = value;
        this.valuemax = valuemax;
        this.length = length;
    }
    @Override
    public void draw() {
        modal.renderer.drawRectBorder(modal.xpos + x*gw(),modal.ypos + y*gh(), length*gw(),gh(),1, UColor.BLACK, modal.config.getHiliteColor());
        modal.renderer.drawRect(x*gw(), y*gh(), (int)((length*gw()) * (float)value/(float)valuemax), gh(), modal.config.getHiliteColor());
    }
}