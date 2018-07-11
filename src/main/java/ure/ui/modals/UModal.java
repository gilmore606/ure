package ure.ui.modals;

import ure.Injector;
import ure.UCommander;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.UCamera;
import ure.ui.View;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * UModal intercepts player commands and (probably) draws UI in response, and returns a value to
 * a callback when it wants to (i.e. when the user is finished).
 *
 */
public class UModal extends View {

    @Inject
    UCommander commander;

    HearModal callback;
    String callbackContext;
    URenderer renderer;
    UCamera camera;
    public int width, height;
    public int cellx,celly;
    public UColor bgColor;
    HashMap<String,TextFrag> texts;

    class TextFrag {
        String name;
        String text;
        int row;
        int col;
        UColor color;

        public TextFrag(String tname, String ttext, int trow, int tcol, UColor tcolor) {
            name = tname;
            text = ttext;
            row = trow;
            col = tcol;
            color = tcolor;
        }
    }

    public UModal(HearModal _callback, String _callbackContext) {
        Injector.getAppComponent().inject(this);
        callback = _callback;
        callbackContext = _callbackContext;
    }

    @Override
    public void draw(URenderer renderer) {
        //
    }

    void DrawContent() {
        commander.printScroll("Hit any key to continue...");
        //for (String textName : texts.keySet()) {
            //TextFrag frag = texts.get(textName);
            // TODO: Fix for new renderer
            //g.setFont(renderer.font);
            //g.setColor(frag.color);
            //g.drawString(frag.text, frag.row * renderer.cellWidth(), ((frag.col + 1) * renderer.cellHeight()) + 0);
        //}
    }

    public void hearCommand(String command) {
        dismiss();
    }

    void dismiss() {
        commander.detachModal();
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, UColor.COLOR_WHITE));
    }
    public void addText(String name, String text, int row, int col, UColor color) {
        addTextFrag(new TextFrag(name, text, row, col, color));
    }
    void addTextFrag(TextFrag frag) {
        texts.put(frag.name, frag);
    }

}
