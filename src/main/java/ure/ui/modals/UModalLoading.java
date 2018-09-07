package ure.ui.modals;

import ure.commands.UCommand;
import ure.sys.GLKey;

public class UModalLoading extends UModal {

    String text;

    public UModalLoading() {
        super(null, "");
        this.text = "Loading...";
        this.colpad = 1;
        this.rowpad = 1;
        this.escapable = false;
        setDimensions(textWidth(text), 1);
        dismissFrameEnd = 0;
    }

    @Override
    public void drawContent() {
        drawString(text, 0, 0, config.getTextColor());
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {

    }
}
