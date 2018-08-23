package ure.ui.modals.widgets;


import ure.ui.RexFile;
import ure.ui.modals.UModal;

public class WidgetRexImage extends Widget {
    RexFile image;
    public float alpha = 1f;
    public WidgetRexImage(UModal modal, int x, int y, String filename) {
        super(modal);
        image = new RexFile(modal.commander.config.getResourcePath() + filename);
        setDimensions(x,y,image.width,image.height);
    }
    @Override
    public void drawMe() {
        image.draw(modal.renderer, alpha, x*modal.gw()+modal.xpos,y*modal.gh()+modal.ypos);
    }
}