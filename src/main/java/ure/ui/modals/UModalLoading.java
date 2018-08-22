package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;

public class UModalLoading extends UModal {

    String text;

    public UModalLoading() {
        super(null, "");
        this.text = "Loading...";
        this.xpad = 1;
        this.ypad = 1;
        this.escapable = false;
        setDimensions(text.length(), 1);
    }

    @Override
    public void drawContent() {
        renderer.drawString(relx(xpad/2), rely(ypad/2), commander.config.getTextColor(), text);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {

    }
}
