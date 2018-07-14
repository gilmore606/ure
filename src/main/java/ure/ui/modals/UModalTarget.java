package ure.ui.modals;

import ure.render.URenderer;
import ure.sys.Entity;

import java.util.ArrayList;

public class UModalTarget extends UModal {

    String prompt;
    ArrayList<Entity> targets;
    boolean shiftFree, visibleOnly;

    int cellx, celly;

    public UModalTarget(String _prompt, HearModalTarget _callback, String _callbackContext, ArrayList<Entity> _targets, boolean _shiftFree, boolean _visibleOnly) {
        super(_callback, _callbackContext);
        prompt = _prompt;
        targets = _targets;
        shiftFree = _shiftFree;
        visibleOnly = _visibleOnly;
        setDimensions(3,3);
    }

    @Override
    public void setDimensions(int x, int y) {
        cellw = x;
        cellh = y;
    }

    @Override
    public void drawFrame(URenderer renderer) {

    }

    @Override
    public void drawContent(URenderer renderer) {

    }
}
