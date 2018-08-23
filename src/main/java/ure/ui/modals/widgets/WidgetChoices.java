package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
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
        this.x = x;
        this.y = y;
        calcPositions();
    }
    void calcPositions() {
        int pos=0;
        positions = new int[choices.length];
        for (int i=0;i<choices.length;i++) {
            positions[i] = pos;
            pos += modal.textWidth(choices[i]) + 1;
        }
        setDimensions(x,y,pos-1,1);
    }
    public void addIcons(Icon[] icons) {
        this.icons = icons;
        calcPositions();
    }
    @Override
    public void draw() {
        for (int i=0;i<choices.length;i++) {
            if (icons != null)
                modal.drawIcon(icons[i], x+positions[i], y);
            modal.drawString(choices[i],x+positions[i]+(icons == null ? 0 : iconSpace), y, (i == selection) ? null : UColor.GRAY, (i == selection && focused) ? modal.config.getHiliteColor() : null);
        }
    }
    @Override
    public void mouseInside(int mousex, int mousey) {
        selection = 0;
        for (int i=1;i<choices.length;i++) {
            if (mousex <= positions[i] && mousex > positions[i-1]) {
                selection = i;
            }
        }
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