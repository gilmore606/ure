package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;

import java.util.ArrayList;

public class UModalEntityPick extends UModal {

    String header;
    UColor bgColor;
    int xpad, ypad;
    ArrayList<Entity> entities;
    int textWidth = 0;
    int selection = 0;

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
        textWidth = width;
        setDimensions(width + 2 + xpad, entities.size() + 2 + ypad);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }

    @Override
    public void drawContent(URenderer renderer) {
        int y = 0;
        for (Entity entity : entities) {
            if (y == selection) {
                renderer.drawRect(gw()+xpos,(y+2)*gh()+ypos, textWidth*gw(), gh(), commander.config.getHiliteColor());
            }
            drawIcon(renderer, entity.icon(), 1, y + 2);
            drawString(renderer, entity.name(), 3, y + 2);
            y++;
        }
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command.id.equals("MOVE_N")) {
            selection--;
            if (selection < 0) {
                if (commander.config.isWrapSelect()) {
                    selection = entities.size() - 1;
                } else {
                    selection = 0;
                }
            }
        } else if (command.id.equals("MOVE_S")) {
            selection++;
            if (selection >= entities.size()) {
                if (commander.config.isWrapSelect()) {
                    selection = 0;
                } else {
                    selection = entities.size() - 1;
                }
            }
        } else if (command.id.equals("PASS")) {
            selectEntity();
        }
    }

    public void selectEntity() {
        dismiss();
        ((HearModalEntityPick)callback).hearModalEntityPick(callbackContext, entities.get(selection));
    }
}
