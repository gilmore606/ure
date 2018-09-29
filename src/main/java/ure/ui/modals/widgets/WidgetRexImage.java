package ure.ui.modals.widgets;


import ure.ui.RexFile;
import ure.ui.modals.UModal;

public class WidgetRexImage extends Widget {
    RexFile image;
    public float alpha = 1f;
    public WidgetRexImage(UModal modal, int col, int row, String filename) {
        super(modal);
        image = new RexFile(filename);
        setDimensions(col,row,image.width,image.height);

    }
    @Override
    public void drawMe() {
        image.draw(modal.renderer, alpha, 0, 0);
    }
}