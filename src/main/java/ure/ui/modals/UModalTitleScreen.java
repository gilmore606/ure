package ure.ui.modals;

import ure.areas.UArea;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.ui.RexFile;

public class UModalTitleScreen extends UModal {

    RexFile logoSplash;
    float alpha;
    int fakeTickCount;
    UArea area;

    String[] options;
    int cursor;

    public UModalTitleScreen(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UColor _bgColor, UArea _area) {
        super(_callback,_callbackContext,_bgColor);
        setDimensions(cellwidth,cellheight);
        logoSplash = new RexFile(commander.config.getResourcePath() + "ure_logo.xp");
        alpha = 0f;
        fakeTickCount = 0;
        area = _area;
        options = new String[]{"Continue", "New World", "Credits"};
        cursor = 0;
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawTitleSplash(renderer);
        drawString(renderer, "Example Quest : Curse of the Feature Creep", 7, 13);
        for (int i=0;i<options.length;i++) {
            drawString(renderer, options[i], 15, 15+i,  (i == cursor) ? null : UColor.COLOR_GRAY, (i == cursor) ? commander.config.getHiliteColor() : null);
        }
    }

    public void drawTitleSplash(URenderer renderer) {
        int xp = (cellw / 2)  - (logoSplash.width / 2);
        int yp = 0;
        logoSplash.draw(renderer, alpha, xp * gw() + xpos, yp * gh() + ypos);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("MOVE_N")) {
                cursor = cursorMove(cursor, -1, options.length);
            } else if (command.id.equals("MOVE_S")) {
                cursor = cursorMove(cursor, 1, options.length);
            } else if (command.id.equals("PASS") || command.id.equals("ESC")) {
                if (cursor == 0)
                    resumeGame();
                else if (cursor == 1)
                    newGame();
            }
        }
    }

    void resumeGame() {
        dismiss();
        ((HearModalTitleScreen)callback).hearModalTitleScreen("resume");
    }

    void newGame() {
        dismiss();
        ((HearModalTitleScreen)callback).hearModalTitleScreen("new");
    }

    @Override
    public void animationTick() {
        super.animationTick();
        alpha += 0.04f;
        if (alpha >1f) alpha = 1f;
        fakeTickCount++;
        if (fakeTickCount > 20) {
            fakeTickCount = 0;
            commander.tickTime();
            area.wakeAllNPCs();
            commander.tickActors();
        }
    }
}
