package ure.ui.modals;

public class UModalNotify extends UModal {

    String text;

    public UModalNotify(String text) {
        super(null, "");
        this.text = text;
        cellw = text.length();
        cellh = 1;
    }
}
