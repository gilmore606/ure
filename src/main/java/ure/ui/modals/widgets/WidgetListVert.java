package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
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
        setDimensions(x, y, modal.longestLine(options), options.length);
    }

    public void addIcons(Icon[] icons) {
        optionIcons = icons;
    }

    @Override
    public void draw() {
        for (int i = 0;i < options.length;i++) {
            if (optionIcons != null)
                modal.drawIcon(optionIcons[i], x, y + i);
            modal.drawString(options[i], x + (optionIcons == null ? 0 : iconSpace + 1), y + i, lit[i] || (i == selection && focused) ? null : UColor.GRAY, (i == selection && focused) ? modal.config.getHiliteColor() : null);
        }
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        selection = Math.max(0, Math.min(options.length - 1, mousey));
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N")) selection = cursorMove(selection, -1, options.length);
            else if (c.id.equals("MOVE_S")) selection = cursorMove(selection, 1, options.length);
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
