package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.ui.modals.widgets.WidgetText;

public class UModalNotify extends UModal {

    public UModalNotify(String text) {
        super(null, "");
        WidgetText widget = new WidgetText(this,0,0,text);
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
