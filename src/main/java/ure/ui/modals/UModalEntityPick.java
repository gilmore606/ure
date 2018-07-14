package ure.ui.modals;

import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;

import java.util.ArrayList;

public class UModalEntityPick extends UModal {

    String header;
    UColor bgColor;
    int xpad, ypad;
    ArrayList<Entity> entities;

    public UModalEntityPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<Entity> _entities, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        header = _header;
        bgColor = _bgColor;
        if (bgColor == null) bgColor = commander.config.getModalBgColor();
        xpad = _xpad;
        ypad = _ypad;
        entities = _entities;
        int width = 0;
        for (Entity entity : entities) {
            if (entity.name().length() > width)
                width = entity.name().length();
        }
        setDimensions(width + 2 + xpad, entities.size() + 2 + ypad);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }

    @Override
    public void drawContent(URenderer renderer) {
        int y = 2;
        for (Entity entity : entities) {
            drawIcon(renderer, entity.icon(), 1, y);
            drawString(renderer, entity.name(), 3, y);
            y++;
        }
    }
}
