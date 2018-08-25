package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.ui.modals.UModal;

public class WidgetText extends Widget {
    String[] lines;
    public UColor color;
    public boolean highlight;
    public WidgetText(UModal modal, int x, int y, String text) {
        super(modal);
        setText(text);
        if (lines != null)
            setDimensions(x,y,modal.longestLine(lines), lines.length);
        else
            setDimensions(x,y,0,0);
    }
    @Override
    public void drawMe() {
        if (lines != null)
            for (int i=0;i<lines.length;i++)
                modal.drawString(lines[i],x,y+i,color, highlight ? hiliteColor() : null);
    }

    public void setText(String text) {
        lines = modal.splitLines(text);
    }
}