package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.HearModalDropdown;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalDropdown;

public class WidgetDropdown extends Widget implements HearModalDropdown {
    String[] choices;
    public int selection;

    public WidgetDropdown(UModal modal, int x, int y, String[] choices, int selected) {
        super(modal);
        this.choices = choices;
        selection = selected;
        focusable = true;
        setDimensions(x, y, modal.longestLine(choices), 1);
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        showDropdown();
    }
    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null)
            if (c.id.equals("PASS"))
                showDropdown();
    }

    @Override
    public void drawMe() {
        drawString(choices[selection], 0, 0, focused ? null : UColor.GRAY, focused ? hiliteColor() : null);
    }

    void showDropdown() {
        UModalDropdown drop = new UModalDropdown(choices, selection, this, "");
        drop.setChildPosition(x, y-selection, modal);
        modal.commander.showModal(drop);
    }

    public void hearModalDropdown(String context, int selection) {
        this.selection = selection;
        modal.widgetChanged(this);
    }
}
