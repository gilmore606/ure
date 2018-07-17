package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;

public class UModalGetString extends UModal {

    String[] prompt;
    String input;
    boolean escapable;
    int maxlength;
    int fieldY;

    int blinkCount = 0;
    boolean blunk = true;

    UColor fieldColor;
    UColor cursorColor;

    public UModalGetString(String _prompt, int _maxlength, boolean _escapable,
                           UColor bgColor, HearModalGetString _callback, String _callbackContext) {
        super(_callback, _callbackContext, bgColor);
        prompt = splitLines(_prompt);
        escapable = _escapable;
        input = "";
        maxlength = _maxlength;
        int width = longestLine(prompt);
        if (maxlength > width)
            width = maxlength;
        int height = 2;
        if (prompt != null)
            height += prompt.length;
        setDimensions(width, height);
        if (prompt == null)
            fieldY = 0;
        else
            fieldY = prompt.length + 1;

        fieldColor = new UColor(commander.config.getHiliteColor());
        cursorColor = new UColor(commander.config.getHiliteColor());
        fieldColor.setAlpha(0.2f);
        cursorColor.setAlpha(1f);
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawStrings(renderer, prompt, 0, 0);
        renderer.drawRect(0 + xpos, fieldY * gh() + ypos, maxlength*gw(), gh(), fieldColor);
        drawString(renderer, input, 0, fieldY);
        if (blunk) {
            renderer.drawRect(xpos + input.length() * commander.config.getTextWidth(), fieldY * gh() + ypos + (gh()/2), commander.config.getTextWidth(), gh()/2, cursorColor);
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        blinkCount++;
        if (blinkCount > commander.config.getCursorBlinkSpeed()) {
            blinkCount = 0;
            blunk = !blunk;
        }
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command != null) {
            if (command.id.equals("ESC") && escapable)
                dismiss();
        }
        if (c == 7) {
            if (!input.isEmpty()) {
                if (input.length() == 1)
                    input = "";
                else
                    input = input.substring(0, input.length() - 1);
            }
        } else if (c == 6) {
            sendInput();
        } else {
            input = input + c.toString();
        }
    }

    public void sendInput() {
        dismiss();
        ((HearModalGetString)callback).hearModalGetString(callbackContext, input);
    }
}
