package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;
import ure.sys.GLKey;

import java.util.ArrayList;

public class UModalEntityPick extends UModal {

    String header;
    UColor bgColor;
    int xpad, ypad;
    ArrayList<Entity> entities;
    ArrayList<String> displaynames;
    boolean showDetail;
    boolean escapable;
    boolean categorize;
    ArrayList<String> categories;
    ArrayList<ArrayList<Entity>> categoryLists;
    int biggestCategoryLength;
    int textWidth = 0;
    int selection = 0;
    int selectionCategory = 0;
    UColor tempHiliteColor, flashColor;

    public UModalEntityPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<Entity> _entities,
                            boolean _showDetail, boolean _escapable, boolean _categorize, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext, _bgColor);
        header = _header;
        xpad = _xpad;
        ypad = _ypad;
        entities = _entities;
        if (!_categorize) displaynames = deDupeEntities(entities);
        showDetail =  _showDetail;
        escapable = _escapable;
        categorize = _categorize;
        if (categorize)
            Categorize();
        int width = 0;
        for (Entity entity : entities) {
            if (entity.getName().length() > width)
                width = entity.getName().length();
        }
        textWidth = width;
        if (showDetail || categorize)
            width += 9;
        int height = 0;
        if (categorize)
            height = Math.max(biggestCategoryLength, categories.size()) + 9 + ypad;
        else
            height = Math.max(6, entities.size() + 2 + ypad);
        setDimensions(width + 2 + xpad, height);
        if (bgColor == null)
            bgColor = commander.config.getModalBgColor();
        setBgColor(bgColor);
        tempHiliteColor = commander.config.getHiliteColor();
        flashColor = new UColor(commander.config.getHiliteColor());
        flashColor.setAlpha(1f);
        dismissFrameEnd = 8;
    }

    public ArrayList<Entity> shownEntities() {
        if (categorize) {
            return categoryLists.get(selectionCategory);
        } else {
            return entities;
        }
    }

    public void Categorize() {
        categories = new ArrayList<>();
        categoryLists = new ArrayList<>();
        for (Entity entity : entities) {
            String cat = entity.getCategory();
            if (categories.contains(cat)) {
                categoryLists.get(categories.indexOf(cat)).add(entity);
            } else {
                categories.add(cat);
                ArrayList<Entity> newList = new ArrayList<Entity>();
                newList.add(entity);
                categoryLists.add(newList);
            }
        }
        for (ArrayList<Entity> theList : categoryLists) {
            if (theList.size() > biggestCategoryLength)
                biggestCategoryLength = theList.size();
        }
    }


    public ArrayList<String> deDupeEntities(ArrayList<Entity> entities) {
        ArrayList<Entity> newent = new ArrayList<>();
        ArrayList<String> displaynames = new ArrayList<>();
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
        return displaynames;
    }

    @Override
    public void drawContent(URenderer renderer) {
        if (header != null)
            drawString(renderer, header, 0, 0);
        int y = 0;
        for (Entity entity : shownEntities()) {
            if (y == selection) {
                renderer.drawRect(gw()+xpos,(y+2)*gh()+ypos, textWidth*gw(), gh(), tempHiliteColor);
            }
            drawIcon(renderer, entity.getIcon(), 1, y + 2);
            drawString(renderer, entity.getName(), 3, y + 2);
            y++;
        }
        if (showDetail) {
            drawString(renderer, shownEntities().get(selection).getName(), 4+textWidth, 2);
            ArrayList<String> details = shownEntities().get(selection).UIdetails(callbackContext);
            int linepos = 4;
            for (String line : details) {
                drawString(renderer, line, 4+textWidth, linepos);
                linepos++;
            }
        }
        if (categorize) {
            int caty = 7;
            if (!showDetail) caty = 1;
            int i =0;
            for (String cat : categories) {
                if (i == selectionCategory) {
                    renderer.drawRect((4 + textWidth) * gw() + xpos, (2 + caty + i) * gh() + ypos, 9 * gw(), gh(), tempHiliteColor);
                    drawString(renderer, cat, 4 + textWidth, 2 + caty + i);
                } else {
                    drawString(renderer, cat, 4+textWidth, 2+caty+i, UColor.COLOR_DARKGRAY);
                }
                i++;
            }
        }
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command == null) return;
        if (command.id.equals("MOVE_N")) {
            selection--;
            if (selection < 0) {
                if (commander.config.isWrapSelect()) {
                    selection = shownEntities().size() - 1;
                } else {
                    selection = 0;
                }
            }
        } else if (command.id.equals("MOVE_S")) {
            selection++;
            if (selection >= shownEntities().size()) {
                if (commander.config.isWrapSelect()) {
                    selection = 0;
                } else {
                    selection = shownEntities().size() - 1;
                }
            }
        } else if (command.id.equals("MOVE_W")) {
            selection = 0;
            selectionCategory--;
            if (selectionCategory < 0) {
                if (commander.config.isWrapSelect()) {
                    selectionCategory = categoryLists.size() - 1;
                } else {
                    selectionCategory = 0;
                }
            }
        } else if (command.id.equals("MOVE_E")) {
            selection = 0;
            selectionCategory++;
            if (selectionCategory >= categoryLists.size()) {
                if (commander.config.isWrapSelect()) {
                    selectionCategory = 0;
                } else {
                    selectionCategory = categoryLists.size() - 1;
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
        ((HearModalEntityPick)callback).hearModalEntityPick(callbackContext, shownEntities().get(selection));
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
