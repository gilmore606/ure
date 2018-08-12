package ure.ui.modals;

import ure.areas.UArea;
import ure.commands.UCommand;
import ure.editors.glyphed.GlyphedModal;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.RexFile;

import java.io.File;

public class UModalTitleScreen extends UModal implements HearModalGetString {

    RexFile logoSplash;
    float alpha;
    int fakeTickCount;
    UArea area;

    String titleMsg = "Example Quest : Curse of the Feature Creep";
    String[] options;
    int cursor;

    double lastActiveTime;
    int hideSeconds = 10;

    public UModalTitleScreen(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UColor _bgColor, UArea _area) {
        super(_callback,_callbackContext,_bgColor);
        setDimensions(cellwidth,cellheight);
        logoSplash = new RexFile(commander.config.getResourcePath() + "ure_logo.xp");
        alpha = 0f;
        fakeTickCount = 0;
        area = _area;
        options = new String[]{"Continue", "New World", "VaultEd", "GlyphEd", "Credits", "Quit"};
        File file = new File(commander.savePath() + "player");
        if (!file.isFile())
            options = new String[]{"New World", "VaultEd", "GlyphEd", "Credits", "Quit"};
        cursor = 0;
        commander.speaker.playBGM(commander.config.getTitleMusic());
        lastActiveTime = System.currentTimeMillis();
    }

    @Override
    public void drawContent() {
        if ((System.currentTimeMillis() - lastActiveTime) < hideSeconds*1000) {
            cursor = mouseToSelection(options.length, 13, cursor);
            drawTitleSplash();
            if (alpha >= 1f) {
                drawString(titleMsg, cellw / 2 - (textWidthInCells(titleMsg) / 2), 11);
                for (int i = 0;i < options.length;i++) {
                    drawString(options[i], 15, 13 + i, (i == cursor) ? null : UColor.GRAY, (i == cursor) ? commander.config.getHiliteColor() : null);
                }
            }
        }
    }

    @Override
    public void drawFrame() {
        if ((System.currentTimeMillis() - lastActiveTime) < hideSeconds*1000) {
            super.drawFrame();
        }
    }

    public void drawTitleSplash() {
        int xp = (cellw / 2)  - (logoSplash.width / 2);
        int yp = -2;
        logoSplash.draw(renderer, alpha, xp * gw() + xpos, yp * gh() + ypos);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        lastActiveTime = System.currentTimeMillis();
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
    @Override
    public void mouseClick() {
        if (System.currentTimeMillis() - lastActiveTime > hideSeconds*1000) {
            lastActiveTime = System.currentTimeMillis();
            return;
        }
        if (alpha < 1f)
            alpha = 1f;
        else
            pickSelection();
    }

    @Override
    public void mouseRightClick() { }

    void pickSelection() {
        if (options[cursor].equals("New World")) {
            UModalGetString smodal = new UModalGetString("Name your character:", 20, true,
                    null, this, "name-new-world");
            commander.showModal(smodal);
        } else if (options[cursor].equals("Credits")) {
            UModalNotify nmodal = new UModalNotify("URE: the unRoguelike Engine\n \nSpunky - metaprogramming, persistence, rendering\nMoycakes - OpenGL\nKapho - QA, content\nGilmore - misc", null, 1, 1);
            commander.showModal(nmodal);
        } else if (options[cursor].equals("VaultEd")) {
            commander.launchVaulted();
        } else if (options[cursor].equals("GlyphEd")) {
            GlyphedModal modal = new GlyphedModal();
            commander.showModal(modal);
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
            commander.letActorsAct();
        }
    }
}
