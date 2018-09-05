package ure.ui.panels;

import ure.commands.CommandHotbar;
import ure.math.UColor;
import ure.things.UThing;

import java.util.ArrayList;

public class HotbarPanel extends UPanel {

    public HotbarPanel(UColor fgColor, UColor bgColor) {
        super(0,0, fgColor, bgColor, null);
    }

    @Override
    public void drawContent() {
        ArrayList<UThing> items = commander.player().getHotbar();
        int cursorx = 0;
        int cursory = 0;
        for (int i=0; i<10; i++) {
            if (i < items.size() && items.get(i) != null) {
                String entry = Integer.toString(i) + ": ";
                drawString(entry, cursorx, cursory, config.getTextGray());
                entry = items.get(i).name();
                drawIcon(items.get(i).icon(), cursorx+1, cursory);
                drawString(entry, cursorx+2, cursory, fgColor);
            }
            cursorx += 9;
            if (i == 4) {
                cursorx = 0;
                cursory = 1;
            }
        }
    }

    @Override
    public void mouseClick() {
        int i = (mouseX()/9) + (mouseY()*5);
        CommandHotbar c = new CommandHotbar("", i);
        c.execute(commander.player());
    }

    @Override
    public void mouseRightClick() {
        int i = (mouseX()/9) + (mouseY()*5);
        commander.player().clearHotbarSlot(i);
    }
}
