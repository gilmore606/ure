package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetButton extends Widget {

    public String label;
    public Icon icon;

    public WidgetButton(UModal modal, int x, int y, String label, Icon icon) {
        super(modal);
        this.label = label;
        this.icon = icon;
        focusable = true;
        setDimensions(x,y,modal.textWidth(label) + (icon == null ? 0 : 1),1);
    }

    @Override
    public void drawMe() {
        if (icon != null)
            drawIcon(icon, 0, 0);
        drawString(label, (icon == null) ? 0 : 1, 0, null, focused ? hiliteColor() : null);
    }
}
