package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;

import java.util.ArrayList;

/**
 * ModalChoices gives the user a few plaintext choices and returns the one picked.
 *
 */
public class UModalChoices extends UModal {

    ArrayList<String> choices;
    int escapeChoice;
    boolean escapable;
    String prompt;
    int selection = 0;

    public UModalChoices(String _prompt, ArrayList<String> _choices, int _escapeChoice, int _defaultChoice,
                         boolean _escapable, UColor bgColor, HearModalChoices _callback, String _callbackContext) {
        super(_callback, _callbackContext, bgColor);
        prompt = _prompt;
        choices = _choices;
        escapeChoice = _escapeChoice;
        escapable = _escapable;
        selection = _defaultChoice;
        int width = prompt.length();
        int cwidth = 0;
        for (String choice : choices) {
            cwidth = (choice.length() + 1);
        }
        if (cwidth > width)
            width = cwidth;
        setDimensions(width, 3);
    }

    @Override
    public void drawContent(URenderer renderer) {
        if (prompt != null)
            drawString(renderer, prompt, 0, 0);
        int xtab = 0;
        int drawSelection = 0;
        for (String choice : choices) {
            if (selection == drawSelection)
                renderer.drawRect(xtab*gw()+xpos,2*gh()+ypos,choice.length()*gw(),gh(),commander.config.getHiliteColor());
            drawString(renderer, choice, xtab, 2);
            xtab += choice.length() + 1;
            drawSelection++;
        }
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command.id.equals("MOVE_W") || command.id.equals("MOVE_N"))
            selection--;
        if (command.id.equals("MOVE_E") || command.id.equals("MOVE_S"))
            selection++;
        if (command.id.equals("PASS"))
            pickSelection();
        if (command.id.equals("ESC")) {
            if (escapeChoice >= 0) {
                selection = escapeChoice;
                pickSelection();
            } else if (escapable) {
                dismiss();
            }
        }

        if (selection < 0) {
            if (commander.config.isWrapSelect())
                selection = choices.size()-1;
            else
                selection = 0;
        } else if (selection >= choices.size()) {
            if (commander.config.isWrapSelect())
                selection = 0;
            else
                selection = choices.size()-1;
        }
    }

    public void pickSelection() {
        dismiss();
        ((HearModalChoices)callback).hearModalChoices(callbackContext, choices.get(selection));
    }
}
