package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.UModal;

public class WidgetHSlider extends Widget {

    public int value, valuemin, valuemax;
    int length;
    boolean showNumber;

    public WidgetHSlider(UModal modal, int x, int y, int length, int value, int valuemin, int valuemax, boolean showNumber) {
        super(modal);
        setDimensions(x,y,length + (showNumber ? 3 : 0),1);
        focusable = true;
        this.value = value;
        this.valuemin = valuemin;
        this.valuemax = valuemax;
        this.length = length;
        this.showNumber = showNumber;
    }

    @Override
    public void drawMe() {
        modal.renderer.drawRectBorder(pixelX(), pixelY(), length*gw(),gh(),focused ? 2 : 1, UColor.BLACK, modal.config.getHiliteColor());
        modal.renderer.drawRect(pixelX(), pixelY(), (int)((length*gw()) * (float)value/(float)valuemax), gh(), modal.config.getHiliteColor());
        if (showNumber)
            modal.drawString(Integer.toString(value), x + length + 1, y, null, focused ? modal.config.getHiliteColor() : null);
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_W")) setValue(Math.max(valuemin, value-1));
            else if (c.id.equals("LATCH_W")) setValue(Math.max(valuemin,value-10));
            else if (c.id.equals("MOVE_S")) setValue(Math.max(valuemin,value-100));
            else if (c.id.equals("MOVE_E")) setValue(Math.min(valuemax, value+1));
            else if (c.id.equals("LATCH_E")) setValue(Math.min(valuemax, value+10));
            else if (c.id.equals("MOVE_N")) setValue(Math.min(valuemax, value+100));
        }
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        float frac = (float)mousePixelX() / (float)(length * gw());
        setValue(valuemin + (int)((float)(valuemax - valuemin) * frac));
    }

    void setValue(int v) {
        value = v;
        modal.widgetChanged(this);
    }
}