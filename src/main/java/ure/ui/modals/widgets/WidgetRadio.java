package ure.ui.modals.widgets;

import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetRadio extends Widget {

    public String label;
    public Icon offIcon;
    public Icon onIcon;
    public boolean on;

    public WidgetRadio(UModal modal, int x, int y, String label, Icon offIcon, Icon onIcon, boolean on) {
        super(modal);
        label = " " + label;
        this.label = label;
        this.on = on;
        this.offIcon = offIcon;
        this.onIcon = onIcon;
        focusable = true;
        setDimensions(x,y,modal.textWidth(label) + 1, 1);
    }

    @Override
    public void drawMe() {
        drawIcon(on ? onIcon : offIcon, 0, 0);
        drawString(label, 1, 0, on ? null : modal.config.getTextGray(), focused ? hiliteColor() : null);
    }

    @Override
    public void pressWidget() {
        on = !on;
        modal.widgetChanged(this);
    }
}
