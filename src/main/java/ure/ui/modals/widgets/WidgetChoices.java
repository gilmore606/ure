package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetChoices extends Widget {
    String[] choices;
    Icon[] icons;
    int iconSpace = 1;
    int[] positions;
    int selection;
    public WidgetChoices(UModal modal, int x, int y, String[] choices) {
        super(modal);
        this.choices = choices;
        focusable = true;
        this.col = x;
        this.row = y;
        calcPositions();
    }
    void calcPositions() {
        int pos=0;
        positions = new int[choices.length];
        for (int i=0;i<choices.length;i++) {
            positions[i] = pos;
            pos += modal.textWidth(choices[i]) + 1;
        }
        setDimensions(col, row,pos-1,1);
    }
    public void addIcons(Icon[] icons) {
        this.icons = icons;
        calcPositions();
    }
    @Override
    public void drawMe() {
        for (int i=0;i<choices.length;i++) {
            if (icons != null)
                drawIcon(icons[i], positions[i], 0);
            drawString(choices[i], positions[i]+(icons == null ? 0 : iconSpace), 0, (i == selection) ? null : grayColor(), (i == selection && focused) ? hiliteColor() : null);
        }
    }
    @Override
    public void mouseInside(int mousex, int mousey) {
        selection = 0;
        for (int i=1;i<choices.length;i++) {
            if (mousex <= positions[i] && mousex > positions[i-1]) {
                selection = i-1;
            }
        }
        if (mousex > positions[positions.length-1])
            selection = positions.length-1;
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_W")) selection = cursorMove(selection, -1, choices.length);
            else if (c.id.equals("MOVE_E")) selection = cursorMove(selection, 1, choices.length);
        }
        super.hearCommand(c, k);
    }
    public String choice() { return choices[selection]; }
}