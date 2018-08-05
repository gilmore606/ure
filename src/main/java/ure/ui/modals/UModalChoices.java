package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;

import java.util.ArrayList;

/**
 * ModalChoices gives the user a few plaintext choices and returns the one picked.
 *
 */
public class UModalChoices extends UModal {

    ArrayList<String> choices;
    int escapeChoice;
    boolean escapable;
    String[] prompt;
    int selection = 0;
    int hiliteX, hiliteY, hiliteW, hiliteH;
    UColor tempHiliteColor;
    UColor flashColor;

    public UModalChoices(String _prompt, ArrayList<String> _choices, int _escapeChoice, int _defaultChoice,
                         boolean _escapable, UColor bgColor, HearModalChoices _callback, String _callbackContext) {
        super(_callback, _callbackContext, bgColor);
        prompt = splitLines(_prompt);
        choices = _choices;
        escapeChoice = _escapeChoice;
        escapable = _escapable;
        selection = _defaultChoice;
        int width = longestLine(prompt);
        int height = 2;
        if (prompt != null)
            height += prompt.length;
        int cwidth = 0;
        for (String choice : choices) {
            cwidth = (choice.length() + 1);
        }
        if (cwidth > width)
            width = cwidth;
        setDimensions(width, height);
        dismissFrameEnd = 8;
        tempHiliteColor = commander.config.getHiliteColor();
        flashColor = new UColor(commander.config.getHiliteColor());
        flashColor.setAlpha(1f);
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawStrings(renderer, prompt, 0, 0);
        int xtab = 0;
        int drawSelection = 0;
        for (String choice : choices) {
            int oldxtab = xtab;
            drawString(renderer, choice, xtab, cellh-1, (selection == drawSelection) ? null : UColor.COLOR_GRAY, (selection == drawSelection) ? tempHiliteColor : null);
            xtab += choice.length() + 1;
            drawSelection++;
            if (mousex < xtab && mousex >= oldxtab && mousey > 0 && mousey <= cellh)
                selection = drawSelection-1;
        }
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("MOVE_W") || command.id.equals("MOVE_N"))
                selection = cursorMove(selection, -1, choices.size());
            if (command.id.equals("MOVE_E") || command.id.equals("MOVE_S"))
                selection = cursorMove(selection, 1, choices.size());
            if (command.id.equals("PASS"))
                pickSelection();
            if (command.id.equals("ESC") && escapable) {
                escape();
            }
        }
    }
    @Override
    public void mouseClick() {
        pickSelection();
    }

    public void pickSelection() {
        dismiss();
        ((HearModalChoices)callback).hearModalChoices(callbackContext, choices.get(selection));
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
