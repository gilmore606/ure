package ure.ui.modals;

import ure.commands.UCommand;
import ure.commands.UCommandMove;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;

/**
 * ModalDirection queries the user for a cardinal direction (or space for no direction, if allowed).
 *
 */
public class UModalDirection extends UModal {

    String prompt;
    boolean acceptNull;

    int cellx, celly;

    String glyphs = "^>v<";
    UColor glyphColor;

    public UModalDirection(String _prompt, boolean _acceptNull, int cellx, int celly, HearModalDirection _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        prompt = _prompt;
        acceptNull = _acceptNull;
        this.cellx = cellx;
        this.celly = celly;
        setDimensions(3,3);
        glyphColor = new UColor(commander.config.getHiliteColor());
        dismissFrameEnd = 10;
    }

    @Override
    public void setDimensions(int col, int row) {
        cellw = col;
        cellh = row;
        width = cellw * gw();
        height = cellh * gh();
        x = (cellx - 1) * commander.config.getTileWidth();
        y = (celly - 1) * commander.config.getTileHeight();
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command.id.startsWith("MOVE")) {
            dismiss();
            ((HearModalDirection) callback).hearModalDirection(callbackContext, ((UCommandMove) command).xdir, ((UCommandMove) command).ydir);
        } else if (command.id.equals("PASS") && acceptNull) {
            dismiss();
            ((HearModalDirection) callback).hearModalDirection(callbackContext, 0, 0);
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }

    @Override
    public void drawFrame() {

    }
    @Override
    public void drawContent() {
        commander.printScroll(prompt);
        int gw = commander.config.getTileWidth();
        int gh = commander.config.getTileHeight();
        renderer.drawGlyph(glyphs.charAt(0), absoluteX() + gw, absoluteY(), glyphColor);
        renderer.drawGlyph(glyphs.charAt(2), absoluteX() + gw, absoluteY() + gh*2, glyphColor);
        renderer.drawGlyph(glyphs.charAt(1), absoluteX() + gw * 2, absoluteY() + gh, glyphColor);
        renderer.drawGlyph(glyphs.charAt(3), absoluteX(), absoluteY() + gh, glyphColor);
    }

    @Override
    public void animationTick() {
        if (dismissed) {
            glyphColor.setAlpha((float)Math.sin(commander.frameCounter * 2f) * 0.5f + 0.6f);
        } else {
            glyphColor.setAlpha((float) Math.sin(commander.frameCounter * 0.14f) * 0.3f + 0.4f);
        }
        super.animationTick();
    }
}
