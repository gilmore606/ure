package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;

public class UModalNotify extends UModal {

    int xpad,ypad;

    public UModalNotify(String text, int xpad, int ypad) {
        super(null, "");
        this.xpad = xpad;
        this.ypad = ypad;
        WidgetText widget = new WidgetText(xpad,ypad,text);
        setDimensions(widget.w + xpad, widget.h + ypad);
        addCenteredWidget(widget);
    }

    @Override
    public void mouseClick() {
        dismiss();
    }
    @Override
    public void hearCommand(UCommand command, GLKey k) {
        dismiss();
    }
}
