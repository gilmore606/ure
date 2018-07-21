package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.ui.RexFile;

public class UModalTitleScreen extends UModal {

    RexFile logoSplash;
    float alpha;

    public UModalTitleScreen(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UColor _bgColor) {
        super(_callback,_callbackContext,_bgColor);
        setDimensions(cellwidth,cellheight);
        logoSplash = new RexFile(commander.config.getResourcePath() + "ure_logo.xp");
        alpha = 0f;
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawTitleSplash(renderer);
    }

    public void drawTitleSplash(URenderer renderer) {
        int xp = (cellw / 2)  - (logoSplash.width / 2);
        int yp = 4;
        logoSplash.draw(renderer, alpha, xp * gw() + xpos, yp * gh() + ypos);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        resumeGame();
    }

    void resumeGame() {
        dismiss();
        ((HearModalTitleScreen)callback).hearModalTitleScreen("resume");
    }

    @Override
    public void animationTick() {
        super.animationTick();
        alpha += 0.04f;
        if (alpha >1f) alpha = 1f;
    }
}
