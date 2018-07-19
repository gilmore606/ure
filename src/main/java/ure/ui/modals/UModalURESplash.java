package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.RexFile;

public class UModalURESplash extends UModal {

    RexFile logo;
    float alpha;
    int waitTick = 0;
    int waitMax = 90;
    float fadetick = 0.04f;

    public UModalURESplash() {
        super(null, "", UColor.COLOR_BLACK);
        logo = new RexFile("ure_logo.xp");
        alpha = 0f;
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        waitTick = 1000;
        alpha = 0.4f;
    }

    @Override
    public void draw(URenderer renderer) {
        logo.draw(renderer, alpha);
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (waitTick < waitMax) {
            alpha += fadetick;
        }
        if (alpha >= 1f) {
            alpha = 1f;
        }
        waitTick++;
        if (waitTick > waitMax) {
            alpha -= fadetick;
            if (alpha <= 0f) {
                dismiss();
            }
        }
    }
}
