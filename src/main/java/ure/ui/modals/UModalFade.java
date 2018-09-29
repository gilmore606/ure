package ure.ui.modals;

import ure.math.UColor;

public class UModalFade extends UModal {

    UColor shade;

    float speed, alpha;
    boolean reversed;

    public UModalFade(HearModalFade _callback, String _callbackContext, float speed) {
        super(_callback,_callbackContext);
        this.speed = speed;
        shade = new UColor(0f,0f,0f,0f);
        reversed = false;
        escapable = false;
        alpha = 0f;
        setDimensions(commander.camera().columns,commander.camera().rows);
    }

    @Override
    public void drawFrame() { }

    @Override
    public void drawContent() {
        renderer.drawRect(0,0,commander.camera().width,commander.camera().height,shade);
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (reversed) {
            alpha -= speed;
            if (alpha <= 0f) {
                escape();
            }
        } else {
            alpha += speed;
            if (alpha >= 1f) {
                reversed = true;
                ((HearModalFade)callback).hearModalFade(callbackContext);
            }
        }
        shade.setAlpha(alpha);
    }
}
