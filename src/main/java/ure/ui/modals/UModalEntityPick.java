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
    ArrayList<String> displaynames;
    boolean showDetail;
    boolean escapable;
    int textWidth = 0;
    int selection = 0;
    UColor tempHiliteColor, flashColor;

    public UModalEntityPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<Entity> _entities,
                            boolean _showDetail, boolean _escapable, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext, _bgColor);
        header = _header;
        xpad = _xpad;
        ypad = _ypad;
        entities = _entities;
        deDupeEntities();
        showDetail =  _showDetail;
        escapable = _escapable;
        int width = 0;
        for (Entity entity : entities) {
            if (entity.getName().length() > width)
                width = entity.getName().length();
        }
        textWidth = width;
        if (showDetail)
            width += 9;
        int height = Math.max(6, entities.size() + 2 + ypad);
        setDimensions(width + 2 + xpad, height);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
        tempHiliteColor = commander.config.getHiliteColor();
        flashColor = new UColor(commander.config.getHiliteColor());
        flashColor.setAlpha(1f);
        dismissFrameEnd = 8;
    }

    public void deDupeEntities() {
        ArrayList<Entity> newent = new ArrayList<>();
        displaynames = new ArrayList<>();
        ArrayList<Integer> totals = new ArrayList<>();
        int i = 0;
        for (Entity entity : entities) {
            boolean gotone = false;
            int ni = 0;
            for (Entity nent: newent) {
                if (nent.getName().equals(entity.getName())) {
                    gotone = true;
                } else if (!gotone) {
                    ni++;
                }
            }
            if (!gotone) {
                newent.add(entity);
                totals.add(1);
                i++;
            } else {
                totals.set(ni, totals.get(ni) + 1);
            }
        }
        entities = newent;
        i = 0;
        for (Entity entity : entities) {
            int total = totals.get(i);
            if (total > 1)
                displaynames.add(Integer.toString(totals.get(i)) + " " + entity.getPlural());
            else
                displaynames.add(entity.getName());
            i++;
        }
    }

    @Override
    public void drawContent(URenderer renderer) {
        if (header != null)
            drawString(renderer, header, 0, 0);
        int y = 0;
        for (Entity entity : entities) {
            if (y == selection) {
                renderer.drawRect(gw()+xpos,(y+2)*gh()+ypos, textWidth*gw(), gh(), tempHiliteColor);
            }
            drawIcon(renderer, entity.getIcon(), 1, y + 2);
            drawString(renderer, displaynames.get(y), 3, y + 2);
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
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }

    public void selectEntity() {
        dismiss();
        ((HearModalEntityPick)callback).hearModalEntityPick(callbackContext, entities.get(selection));
    }

    @Override
    public void animationTick() {
        if (dismissed) {
            if ((dismissFrames % 2) == 0) {
                tempHiliteColor = commander.config.getModalBgColor();
            } else {
                tempHiliteColor = flashColor;
            }
        }
        super.animationTick();
    }
}
