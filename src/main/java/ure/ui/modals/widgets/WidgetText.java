package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.ui.modals.UModal;

public class WidgetText extends Widget {
    String[] lines;
    public UColor color;
    public boolean highlight;
    public WidgetText(UModal modal, int x, int y, String text) {
        super(modal);
        lines = modal.splitLines(text);
        setDimensions(x,y,modal.longestLine(lines), lines.length);
    }
    @Override
    public void drawMe() {
        for (int i=0;i<lines.length;i++)
            modal.drawString(lines[i],x,y+i,color, highlight ? modal.config.getHiliteColor() : null);
    }
}