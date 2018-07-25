package ure.ui.modals;

import ure.areas.UArea;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.ui.RexFile;
import ure.ui.USpeaker;

import java.io.File;

public class UModalTitleScreen extends UModal implements HearModalGetString {

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
        options = new String[]{"Continue", "New World", "Credits", "Quit"};
        File file = new File(commander.savePath() + "player");
        if (!file.isFile())
            options = new String[]{"New World", "Credits", "Quit"};
        cursor = 0;
        commander.speaker.playBGM(commander.config.getTitleMusic());
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawTitleSplash(renderer);
        if (alpha >= 1f) {
            drawString(renderer, "Example Quest : Curse of the Feature Creep", 7, 13);
            for (int i = 0;i < options.length;i++) {
                drawString(renderer, options[i], 15, 15 + i, (i == cursor) ? null : UColor.COLOR_GRAY, (i == cursor) ? commander.config.getHiliteColor() : null);
            }
        }
    }

    public void drawTitleSplash(URenderer renderer) {
        int xp = (cellw / 2)  - (logoSplash.width / 2);
        int yp = 0;
        logoSplash.draw(renderer, alpha, xp * gw() + xpos, yp * gh() + ypos);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (alpha < 1f) {
            alpha = 1f;
        } else if (command != null) {
            if (command.id.equals("MOVE_N")) {
                cursor = cursorMove(cursor, -1, options.length);
            } else if (command.id.equals("MOVE_S")) {
                cursor = cursorMove(cursor, 1, options.length);
            } else if (command.id.equals("PASS")) {
                pickSelection();
            }
        }
    }

    void pickSelection() {
        if (options[cursor].equals("New World")) {
            UModalGetString smodal = new UModalGetString("Name your character:", 20, true,
                                    null, this, "name-new-world");
            commander.showModal(smodal);
        } else {
            dismiss();
            ((HearModalTitleScreen) callback).hearModalTitleScreen(options[cursor], null);
        }
    }

    public void hearModalGetString(String context, String input) {
        if (context.equals("name-new-world")) {
            dismiss();
            ((HearModalTitleScreen) callback).hearModalTitleScreen("New World", input);
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        area.animationTick();
        alpha += 0.02f;
        if (alpha >1f) alpha = 1f;
        fakeTickCount++;
        if (fakeTickCount > 20) {
            fakeTickCount = 0;
            commander.tickTime();
            commander.tickActors();
        }
    }
}
