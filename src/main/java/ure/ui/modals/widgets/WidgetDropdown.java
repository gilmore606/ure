package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetDropdown extends Widget {
    String[] options;
    Icon[] optionIcons;
    int iconSpace = 1;
    public int selection;

    public WidgetDropdown(UModal modal, int x, int y, String[] options, int selected) {
        super(modal);
        this.options = options;
        selection = selected;
        focusable = true;
        setDimensions(x, y, modal.longestLine(options), 1);
    }

    public void addIcons(Icon[] icons) {
        optionIcons = icons;
    }

    @Override
    public void drawMe() {
        if (!focused) {
            drawString(options[selection], 0, 0, UColor.GRAY);
        }
    }

    @Override
    public void loseFocus() {
        super.loseFocus();

    }

    @Override
    public void gainFocus() {
        super.gainFocus();

    }
}
