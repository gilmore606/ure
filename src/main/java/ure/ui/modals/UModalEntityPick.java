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
    boolean showDetail;
    int textWidth = 0;
    int selection = 0;

    public UModalEntityPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<Entity> _entities,
                            boolean _showDetail, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        header = _header;
        bgColor = _bgColor;
        if (bgColor == null) bgColor = commander.config.getModalBgColor();
        xpad = _xpad;
        ypad = _ypad;
        entities = _entities;
        showDetail =  _showDetail;
        int width = 0;
        for (Entity entity : entities) {
            if (entity.getName().length() > width)
                width = entity.getName().length();
        }
        textWidth = width;
        if (showDetail)
            width += 9;
        setDimensions(width + 2 + xpad, entities.size() + 2 + ypad);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
    }

    @Override
    public void drawContent(URenderer renderer) {
        if (header != null)
            drawString(renderer, header, 0, 0);
        int y = 0;
        for (Entity entity : entities) {
            if (y == selection) {
                renderer.drawRect(gw()+xpos,(y+2)*gh()+ypos, textWidth*gw(), gh(), commander.config.getHiliteColor());
            }
            drawIcon(renderer, entity.getIcon(), 1, y + 2);
            drawString(renderer, entity.getName(), 3, y + 2);
            y++;
        }
        if (showDetail) {
            drawString(renderer, entities.get(selection).getName(), 4+textWidth, 2);
            String[] details = entities.get(selection).UIdetails(callbackContext);
            int linepos = 4;
            for (String line : details) {
                drawString(renderer, line, 4+textWidth, linepos);
                linepos++;
            }
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
