package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.things.UThing;

import java.util.ArrayList;

public class UModalEquipPick extends UModal {

    UColor bgColor;
    ArrayList<UThing> things;
    UThing equipped;
    boolean showDetail;
    int textWidth = 0;
    int selection = 0;
    UColor tempHiliteColor, flashColor;

    public UModalEquipPick(ArrayList<UThing> _things, UThing _equipped, boolean _showDetail, HearModalEquipPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        things = _things;
        equipped = _equipped;
        showDetail = _showDetail;
        textWidth = 0;
        for (UThing thing : things) {
            if (thing != null) {
                int len = thing.name().length();
                if (len > textWidth) textWidth = len;
            }
        }
        width = textWidth + 2;
        int height = Math.max(3, things.size());
        if (showDetail) {
            width += 10;
            height = Math.max(height, 12);
        }
        setDimensions(width + 2, height);
        if (bgColor == null)
            bgColor = config.getModalBgColor();
        setBgColor(bgColor);
        tempHiliteColor = config.getHiliteColor();
        flashColor = new UColor(config.getHiliteColor());
        flashColor.setAlpha(1f);
        dismissFrameEnd = 8;
        int i = 0;
        for (UThing thing : things) {
            if (thing == equipped) {
                selection = i;
            }
            i++;
        }
    }

    @Override
    public void drawContent() {
        selection = mouseToSelection(things.size(), 0, selection);
        int y = 0;
        for (UThing thing : things) {
            String n;
            if (thing == null) {
                n = "<nothing>";
            } else {
                drawIcon(thing.getIcon(), 1, y);
                n = thing.getName();
            }
            if (thing == equipped)
                drawTile(config.getUiCheckGlyph().charAt(0), 2, y, config.getHiliteColor());
            drawString(n, 3, y, (y == selection || thing == equipped )? null : UColor.GRAY, (y == selection) ? tempHiliteColor : null);
            y++;
        }
        if (showDetail) {
            showDetail(equipped, textWidth, 0);
            if (things.get(selection) != equipped)
                showDetail(things.get(selection), textWidth, 5);
        }
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command == null) return;
        if (command.id.equals("MOVE_N")) {
            selection = cursorMove(selection, -1, things.size());
        } else if (command.id.equals("MOVE_S")) {
            selection = cursorMove(selection, 1, things.size());
        } else if (command.id.equals("PASS")) {
            selectEquip();
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }

    @Override
    public void mouseClick() { selectEquip(); }

    void selectEquip() {
        dismiss();
        UThing thing = things.get(selection);
        if (thing == equipped)
            dismissFrameEnd = 0;
        if (thing != null) {
            ((HearModalEquipPick) callback).hearModalEquipPick(callbackContext, things.get(selection));
        } else {
            ((HearModalEquipPick) callback).hearModalEquipPick("unequip", equipped);
        }
    }
}
