package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.widgets.WidgetText;

import java.util.ArrayList;

public class UModalDropdown extends UModal {

    ArrayList<WidgetText> items;
    int selection;

    public UModalDropdown(String[] choices, int selected, HearModalDropdown _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        selection = selected;
        items = new ArrayList<>();
        for (int i=0;i<choices.length;i++) {
            WidgetText item = new WidgetText(this, 0, i, choices[i]);
            item.color = UColor.GRAY;
            addWidget(item);
            items.add(item);
        }
        moveSelection(selection);
        sizeToWidgets();
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("ESC")) {
                escape();
            } else if (c.id.equals("PASS")) {
                select();
            } else if (c.id.equals("MOVE_N")) {
                moveSelection(cursorMove(selection, -1, items.size()));
            } else if (c.id.equals("MOVE_S")) {
                moveSelection(cursorMove(selection, 1, items.size()));
            }
        }
    }

    @Override
    public void updateMouse() {
        super.updateMouse();
        if (isMouseInside())
            moveSelection(mousey);
    }

    @Override
    public void mouseClick() {
        if (isMouseInside())
            select();
    }

    void moveSelection(int newselection) {
        items.get(selection).color = UColor.GRAY;
        items.get(selection).highlight = false;
        selection = newselection;
        items.get(selection).color = null;
        items.get(selection).highlight = true;
    }

    void select() {
        items.get(selection).dismissFlash = true;
        dismiss();
        ((HearModalDropdown)callback).hearModalDropdown(callbackContext, selection);
    }
}
