package ure.ui.modals;

import ure.actors.actions.UAction;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.things.UThing;

import java.util.ArrayList;
import java.util.HashMap;

public class UModalEntityPick extends UModal implements HearModalStringPick {

    String header;
    UColor bgColor;
    int xpad, ypad;
    ArrayList<Entity> entities;
    ArrayList<String> displaynames;
    boolean showDetail;
    boolean escapable;
    boolean categorize;
    boolean selectForVerbs;
    ArrayList<String> categories;
    ArrayList<ArrayList<Entity>> categoryLists;
    ArrayList<ArrayList<String>> categoryItemNames;
    int biggestCategoryLength;
    int textWidth = 0;
    int selection = 0;
    int selectionCategory = 0;
    UColor tempHiliteColor, flashColor;

    HashMap<String,UAction> contextActions;

    public UModalEntityPick(String _header, UColor _bgColor, int _xpad, int _ypad, ArrayList<Entity> _entities,
                            boolean _showDetail, boolean _escapable, boolean _categorize, boolean _selectForVerbs, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext, _bgColor);
        header = _header;
        xpad = _xpad;
        ypad = _ypad;
        entities = _entities;
        if (!_categorize) displaynames = deDupeEntities(entities);
        showDetail =  _showDetail;
        escapable = _escapable;
        categorize = _categorize;
        selectForVerbs = _selectForVerbs;
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
        categoryItemNames = new ArrayList<>();
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
        int i = 0;
        for (ArrayList<Entity> theList : categoryLists) {
            if (theList.size() > biggestCategoryLength)
                biggestCategoryLength = theList.size();
            ArrayList<String> names = deDupeEntities(theList);
            categoryItemNames.add(names);
            System.out.println("names " + categories.get(i) + " = " + Integer.toString(names.size()) + " items " + Integer.toString(theList.size()));
            i++;
        }
    }


    public ArrayList<String> deDupeEntities(ArrayList<Entity> sourceEntities) {
        ArrayList<Entity> newent = new ArrayList<>();
        ArrayList<String> displaynames = new ArrayList<>();
        ArrayList<Integer> totals = new ArrayList<>();
        ArrayList<Entity> writeList = sourceEntities;
        int i = 0;
        for (Entity entity : sourceEntities) {
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
        sourceEntities = newent;
        i = 0;
        writeList.clear();
        for (Entity entity : sourceEntities) {
            int total = totals.get(i);
            if (total > 1)
                displaynames.add(Integer.toString(totals.get(i)) + " " + entity.getPlural());
            else
                displaynames.add(entity.getName());
            i++;
            writeList.add(entity);
        }
        return displaynames;
    }

    @Override
    public void drawContent(URenderer renderer) {
        if (header != null)
            drawString(renderer, header, 0, 0);
        int y = 0;
        for (Entity entity : shownEntities()) {
            drawIcon(renderer, entity.getIcon(), 1, y + 2);
            String n = entity.getName();
            if (categorize)
                n = (categoryItemNames.get(selectionCategory)).get(y);
            drawString(renderer, n, 3, y + 2, y == selection ? null : UColor.COLOR_GRAY, (y == selection) ? tempHiliteColor : null);
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
        if (categorize && !dismissed) {
            int caty = 7;
            if (!showDetail) caty = 1;
            int i =0;
            for (String cat : categories) {
                drawString(renderer, cat, 4+textWidth, 2+caty+i, (i == selectionCategory) ? null : UColor.COLOR_GRAY,
                        (i == selectionCategory) ? tempHiliteColor : null);
                i++;
            }
        }
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command == null) return;
        if (command.id.equals("MOVE_N")) {
            selection = cursorMove(selection, -1, shownEntities().size());
        } else if (command.id.equals("MOVE_S")) {
            selection = cursorMove(selection, 1, shownEntities().size());
        } else if (command.id.equals("MOVE_W")) {
            selection = 0;
            selectionCategory = cursorMove(selectionCategory, -1, categoryLists.size());
        } else if (command.id.equals("MOVE_E")) {
            selection = 0;
            selectionCategory = cursorMove(selectionCategory, 1, categoryLists.size());
        } else if (command.id.equals("PASS")) {
            selectEntity();
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }

    void selectEntity() {
        if (selectForVerbs) {
            UThing thing = (UThing)(shownEntities().get(selection));
            contextActions = thing.contextActions(commander.player());
            if (contextActions != null) {
                ArrayList<String> verbs = new ArrayList<>();
                for (String v : contextActions.keySet())
                    verbs.add(v);
                UModalStringPick smodal = new UModalStringPick(thing.getName() + ":",null,0,0, verbs, true, this, "contextaction");
                commander.showModal(smodal);
            }
        } else {
            dismiss();
            ((HearModalEntityPick) callback).hearModalEntityPick(callbackContext, shownEntities().get(selection));
        }
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

    public void hearModalStringPick(String context, String selection) {
        dismiss();
        UAction action = contextActions.get(selection);
        if (action != null)
            commander.player().doAction(action);
    }
}
