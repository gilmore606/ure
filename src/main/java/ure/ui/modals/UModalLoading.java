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
        setDimensions(text.length(), 1);
    }

    @Override
    public void drawContent() {
        renderer.drawString(0, 0, commander.config.getTextColor(), text);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {

    }
}
