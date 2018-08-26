package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;
import ure.sys.GLKey;

import java.util.ArrayList;

public class UModalTarget extends UModal {

    String prompt;
    ArrayList<Entity> targets;
    boolean shiftFree, visibleOnly;

    String glyphs = "v<^>";
    UColor glyphColor;

    int cellx, celly;

    public UModalTarget(String _prompt, HearModalTarget _callback, String _callbackContext,
                        ArrayList<Entity> _targets, boolean _shiftFree, boolean _visibleOnly) {
        super(_callback, _callbackContext);
        prompt = _prompt;
        targets = _targets;
        shiftFree = _shiftFree;
        visibleOnly = _visibleOnly;
        setDimensions(3,3);
        cellx = commander.player().areaX();
        celly = commander.player().areaY();
        glyphColor = new UColor(commander.config.getHiliteColor());
    }

    @Override
    public void setDimensions(int x, int y) {
        cellw = x;
        cellh = y;
    }

    @Override
    public void drawFrame() {

    }

    @Override
    public void drawContent() {
        commander.printScroll(prompt);
        int camx = cellx - commander.modalCamera().leftEdge;
        int camy = celly - commander.modalCamera().topEdge;
        renderer.drawGlyph(glyphs.charAt(0), (camx) * gw(), (camy-1)*gh(), glyphColor);
        renderer.drawGlyph(glyphs.charAt(2), (camx) * gw(), (camy+1)*gh(), glyphColor);
        renderer.drawGlyph(glyphs.charAt(3), (camx - 1) * gw(), camy * gh(), glyphColor);
        renderer.drawGlyph(glyphs.charAt(1), (camx + 1) * gw(), camy * gh(), glyphColor);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command.id.equals("MOVE_N"))
            celly -= 1;
        if (command.id.equals("MOVE_S"))
            celly += 1;
        if (command.id.equals("MOVE_W"))
            cellx -= 1;
        if (command.id.equals("MOVE_E"))
            cellx += 1;
        if (command.id.equals("ESC"))
            escape();
        if (command.id.equals("PASS"))
            returnSelection();
    }

    public void returnSelection() {
        dismiss();
        ((HearModalTarget)callback).hearModalTarget(callbackContext, null, cellx, celly);
    }

    @Override
    public void animationTick() {
        glyphColor.setAlpha((float)Math.sin(commander.frameCounter * 0.14f) * 0.3f + 0.4f);
        super.animationTick();
    }
}
