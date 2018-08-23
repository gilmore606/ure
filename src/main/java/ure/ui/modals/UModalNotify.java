package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;

public class UModalNotify extends UModal {

    public UModalNotify(String text) {
        super(null, "");
        WidgetText widget = new WidgetText(0,0,text);
        addWidget(widget);
        sizeToWidgets();
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
