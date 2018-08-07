package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;

import java.util.ArrayList;

public class UModalStringPick extends UModal {

    String header;
    UColor bgColor;
    int xpad, ypad;
    ArrayList<String> choices;
    boolean escapable;
    int textWidth = 0;
    int selection = 0;
    UColor tempHiliteColor, flashColor;

    public UModalStringPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<String> _choices,
                            boolean _escapable, HearModalStringPick _callback, String _callbackContext) {
        super(_callback, _callbackContext, _bgColor);
        header = _header;
        xpad = _xpad;
        ypad = _ypad;
        choices = _choices;
        escapable = _escapable;
        int width = 0;
        for (String choice : choices) {
            if (choice.length() > width)
                width = choice.length();
        }
        textWidth = width;
        int height = Math.max(4, choices.size() + 2 + ypad);
        setDimensions(width + 2 + xpad, height);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
        tempHiliteColor = commander.config.getHiliteColor();
        flashColor = new UColor(commander.config.getHiliteColor());
        flashColor.setAlpha(1f);
        dismissFrameEnd = 8;
    }

    @Override
    public void drawContent() {
        selection = mouseToSelection(choices.size(), 2, selection);
        if (header != null)
            drawString(header, 0, 0);
        int y = 0;
        for (String choice : choices) {
            drawString(choices.get(y), 3, y+2, y == selection ? null : UColor.COLOR_GRAY, (y == selection) ? tempHiliteColor : null);
            y++;
        }
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command == null) return;
        if (command.id.equals("MOVE_N")) {
            selection = cursorMove(selection, -1, choices.size());
        } else if (command.id.equals("MOVE_S")) {
            selection = cursorMove(selection, 1, choices.size());
        } else if (command.id.equals("PASS")) {
            selectChoice();
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }
    @Override
    public void mouseClick() { selectChoice(); }

    public void selectChoice() {
        dismiss();
        ((HearModalStringPick)callback).hearModalStringPick(callbackContext, choices.get(selection));
    }

    @Override
    public void animationTick() {
        if (dismissed) {
            if ((dismissFrames % 2) == 0) {
                tempHiliteColor = commander.config.getModalBgColor();
            } else {
                tempHiliteColor = flashColor;
            }
        }
        super.animationTick();
    }
}
