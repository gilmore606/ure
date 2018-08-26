package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetListVert extends Widget {
    String[] options;
    Icon[] optionIcons;
    boolean[] lit;
    int iconSpace = 1;
    public int selection;

    public WidgetListVert(UModal modal, int x, int y, String[] options) {
        super(modal);
        this.options = options;
        lit = new boolean[options.length];
        for (int i=0;i<options.length;i++) { lit[i] = false; }
        focusable = true;
        setDimensions(x, y, modal.longestLine(options) + 2, options.length);
    }

    public void addIcons(Icon[] icons) {
        optionIcons = icons;
    }

    @Override
    public void drawMe() {
        for (int i = 0;i < options.length;i++) {
            if (optionIcons != null)
                drawIcon(optionIcons[i], 0, i);
            drawString(options[i], (optionIcons == null ? 0 : iconSpace + 1), i, lit[i] || (i == selection && focused) ? null : grayColor(), (i == selection && focused) ? hiliteColor() : null);
        }
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        select(Math.max(0, Math.min(options.length - 1, mousey)));
    }

    void select(int newselection) {
        selection = newselection;
        modal.widgetChanged(this);
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N")) select(cursorMove(selection, -1, options.length));
            else if (c.id.equals("MOVE_S")) select(cursorMove(selection, 1, options.length));
        }
        super.hearCommand(c, k);
    }

    public void lightOption(int i) {
        lit[i] = true;
    }
    public void toggleOption(int i) {
        lit[i] = !lit[i];
    }
    public void dimOption(int i) {
        lit[i] = false;
    }
    public boolean lit(int i) {
        return lit[i];
    }
    public void lightAll() {
        for (int i=0;i<options.length;i++) lit[i] = true;
    }

    public String choice() {
        return options[selection];
    }
}
