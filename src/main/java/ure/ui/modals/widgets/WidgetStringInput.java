package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.modals.UModal;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

public class WidgetStringInput extends Widget {

    public String text;
    int maxlength;
    boolean blunk;
    int blinkCount = 0;
    public boolean hitEnter = false;

    public WidgetStringInput(UModal modal, int x, int y, int width, String text, int maxlength) {
        super(modal);
        this.text = text;
        this.maxlength = maxlength;
        setDimensions(x,y,width,1);
        focusable = true;
    }

    @Override
    public void drawMe() {
        if (focused)
            modal.renderer.drawRect(pixelX(), pixelY(), w*gw(), gh(), hiliteColor());
        drawString(text, 0, 0);
        if (blunk)
            modal.renderer.drawRect(pixelX() + modal.renderer.stringWidth(text), pixelY() + (gh()*2)/3, modal.config.getTextWidth(), gh()/3, modal.config.getTextColor());
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (k.k == GLFW_KEY_BACKSPACE) {
            if (!text.isEmpty()) {
                if (text.length() == 1)
                    text = "";
                else
                    text = text.substring(0, text.length() - 1);
                modal.speaker.playUI(modal.config.soundKeystroke);
            } else
                modal.speaker.playUI(modal.config.soundBumpLimit);
        } else if (k.k == GLFW_KEY_ENTER) {
            hitEnter();
        } else {
            String typed = k.typed();
            if (typed != null) {
                text = text + typed;
                modal.speaker.playUI(modal.config.soundKeystroke);
            }
        }
    }

    void hitEnter() {
        hitEnter = true;
        modal.widgetChanged(this);
    }

    @Override
    public void animationTick() {
        super.animationTick();
        blinkCount++;
        if (blinkCount > modal.config.getCursorBlinkSpeed()) {
            blinkCount = 0;
            blunk = !blunk;
        }
    }
}
