package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;

public class UModalQuantity extends UModal {

    String[] prompt;
    boolean escapable;

    int min, max, count;
    int numberX;
    int barwidth;

    public UModalQuantity(String _prompt, int _min, int _max, boolean _escapable, UColor _bgColor, HearModalQuantity _callback, String _callbackContext) {
        super(_callback,_callbackContext,_bgColor);
        prompt = splitLines(_prompt);
        escapable = _escapable;
        min = _min;
        max = _max;
        count = min;
        barwidth = 15;
        int width = Math.max(longestLine(prompt)+1, barwidth);
        int height = prompt.length + 5;
        setDimensions(width,height);
        numberX = width/2-1;
    }

    @Override
    public void drawContent() {
        if (mousey == 5 && mousex >0 && mousex <= barwidth) {
            float f = (commander.mouseX() - xpos - gw()) / (float)(barwidth * gw());
            count = (int)(f * (float)(max-min)) + min;
            count = Math.max(min,count);
            count = Math.min(max,count);
        }
        drawStrings(prompt, 0, 0);
        drawString(Integer.toString(count), numberX, 3, commander.config.getTextColor(), commander.config.getHiliteColor());
        for (int i=0;i<barwidth;i++) {
            char glyph = '-';
            int countslice = (max-min)/barwidth;
            if (count > i*countslice)
                glyph = '#';
            drawTile(glyph,i+1,5,commander.config.getHiliteColor());
        }
    }

    @Override
    public void mouseClick() { sendInput(); }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("ESC") && escapable)
                escape();
            else if (command.id.equals("MOVE_W"))
                count = cursorMove(count,-1, max);
            else if (command.id.equals("MOVE_E"))
                count = cursorMove(count, 1, max);
            else if (command.id.equals("MOVE_N"))
                count = cursorMove(count,10,max);
            else if (command.id.equals("MOVE_S"))
                count = cursorMove(count, -10, max);
            else if (command.id.equals("PASS"))
                sendInput();
        }
    }

    public void sendInput() {
        dismiss();
        ((HearModalQuantity)callback).hearModalQuantity(callbackContext, count);
    }
}
