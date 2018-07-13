package ure.ui.modals;

import ure.commands.UCommand;
import ure.commands.UCommandMove;
import ure.render.URenderer;

/**
 * ModalDirection queries the user for a cardinal direction (or space for no direction, if allowed).
 *
 */
public class UModalDirection extends UModal {

    String prompt;
    boolean acceptNull;

    public UModalDirection(String _prompt, boolean _acceptNull, HearModalDirection _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        prompt = _prompt;
        acceptNull = _acceptNull;
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command.id.startsWith("MOVE")) {
            ((HearModalDirection) callback).hearModalDirection(callbackContext, ((UCommandMove) command).xdir, ((UCommandMove) command).ydir);
            dismiss();
        } else if (command.id.equals("PASS")) {
            ((HearModalDirection) callback).hearModalDirection(callbackContext, 0, 0);
            dismiss();
        }
    }

    @Override
    public void draw(URenderer renderer) {
        commander.printScroll(prompt);
    }
}
