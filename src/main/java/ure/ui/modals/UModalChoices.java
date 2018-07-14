package ure.ui.modals;

import ure.math.UColor;
import ure.render.URenderer;

import java.util.ArrayList;

/**
 * ModalChoices gives the user a few plaintext choices and returns the one picked.
 *
 */
public class UModalChoices extends UModal {

    ArrayList<String> choices;
    String escapeChoice;
    String prompt;
    int selection = 0;

    public UModalChoices(String _prompt, ArrayList<String> _choices, String _escapeChoice,
                         UColor bgColor, HearModalChoices _callback, String _callbackContext) {
        super(_callback, _callbackContext, bgColor);
        prompt = _prompt;
        choices = _choices;
        escapeChoice = _escapeChoice;
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
}
