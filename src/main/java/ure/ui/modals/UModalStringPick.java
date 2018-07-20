package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;

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
        int height = Math.max(6, choices.size() + 2 + ypad);
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
    public void drawContent(URenderer renderer) {
        if (header != null)
            drawString(renderer, header, 0, 0);
        int y = 0;
        for (String choice : choices) {
            if (y == selection) {
                renderer.drawRect(gw()+xpos,(y+2)*gh()+ypos, textWidth*gw(), gh(), tempHiliteColor);
            }
            drawString(renderer, choices.get(y), 3, y+2);
            y++;
        }
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command == null) return;
        if (command.id.equals("MOVE_N")) {
            selection--;
            if (selection < 0) {
                if (commander.config.isWrapSelect()) {
                    selection = choices.size() - 1;
                } else {
                    selection = 0;
                }
            }
        } else if (command.id.equals("MOVE_S")) {
            selection++;
            if (selection >= choices.size()) {
                if (commander.config.isWrapSelect()) {
                    selection = 0;
                } else {
                    selection = choices.size() - 1;
                }
            }
        } else if (command.id.equals("PASS")) {
            selectChoice();
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }

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
