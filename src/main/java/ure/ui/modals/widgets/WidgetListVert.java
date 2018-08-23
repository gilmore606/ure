package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetListVert extends Widget {
    String[] options;
    Icon[] optionIcons;
    int iconSpace = 1;
    int selection;

    public WidgetListVert(UModal modal, int x, int y, String[] options) {
        super(modal);
        this.options = options;
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
            modal.drawString(options[i], x + (optionIcons == null ? 0 : iconSpace + 1), y + i, (i == selection) ? null : UColor.GRAY, (i == selection && focused) ? modal.config.getHiliteColor() : null);
        }
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        selection = Math.max(0, Math.min(options.length - 1, mousey));
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        mouseInside(mousex, mousey);
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_N")) selection = modal.cursorMove(selection, -1, options.length);
            else if (c.id.equals("MOVE_S")) selection = modal.cursorMove(selection, 1, options.length);
            else if (c.id.equals("PASS")) modal.widgetClick(this, 0, selection);
        }
    }

    public String choice() {
        return options[selection];
    }
}
