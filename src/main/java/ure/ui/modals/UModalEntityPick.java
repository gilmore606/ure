package ure.ui.modals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.actions.UAction;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.things.UThing;

import java.util.ArrayList;
import java.util.HashMap;

public class UModalEntityPick extends UModal implements HearModalStringPick {

    String header;
    ArrayList<Entity> entities;
    ArrayList<String> displaynames;
    boolean showDetail;
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

    private Log log = LogFactory.getLog(UModalEntityPick.class);

    public UModalEntityPick(String _header, ArrayList<Entity> _entities,
                            boolean _showDetail, boolean _categorize, boolean _selectForVerbs, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        header = _header;
        entities = _entities;
        if (!_categorize) displaynames = deDupeEntities(entities);
        showDetail =  _showDetail;
        categorize = _categorize;
        selectForVerbs = _selectForVerbs;
        if (categorize)
            Categorize();
        int width = 0;
        for (Entity entity : entities) {
            int len = textWidth(entity.name());
            if (len > width) width = len;
        }
        textWidth = width;
        if (showDetail || categorize)
            width += Math.max(12, width+1);
        int height = 0;
        if (showDetail)
            height += 8;
        if (categorize)
            height += categories.size();
        int listsize = entities.size();
        if (categorize) {
            listsize = 0;
            for (ArrayList<Entity> cat : categoryLists) {
                int len = cat.size();
                if (len > listsize) listsize = len;
            }
        }
        height = Math.max(height, listsize+1);
        setDimensions(width + 1 + xpad*2, height + 1 + ypad*2);
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
            log.debug("names " + categories.get(i) + " = " + Integer.toString(names.size()) + " items " + Integer.toString(theList.size()));
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
                displaynames.add(entity.name());
            i++;
            writeList.add(entity);
        }
        return displaynames;
    }

    @Override
    public void drawContent() {
        selection = mouseToSelection(shownEntities().size(), 2, selection, 0, 12);
        int oldCategory = selectionCategory;
        if (categorize) {
            selectionCategory = mouseToSelection(categoryLists.size(), 9, selectionCategory, 3 + textWidth, 100);
            if (selectionCategory != oldCategory) selection = 0;
        }
        if (header != null)
            drawString(header, 0, 0);
        int y = 0;
        for (Entity entity : shownEntities()) {
            drawIcon(entity.icon(), 1, y + 2);
            String n = entity.name();
            if (categorize)
                n = (categoryItemNames.get(selectionCategory)).get(y);
            drawString(n, 3, y + 2, y == selection ? null : UColor.GRAY, (y == selection) ? tempHiliteColor : null);
            y++;
        }
        if (showDetail) showDetail(shownEntities().get(selection),4+textWidth,2);
        if (categorize && !dismissed) {
            int caty = 7;
            if (!showDetail) caty = 1;
            int i =0;
            for (String cat : categories) {
                drawString(cat, 4+textWidth, 2+caty+i, (i == selectionCategory) ? null : UColor.GRAY,
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
            if (categoryLists != null)
                selectionCategory = cursorMove(selectionCategory, -1, categoryLists.size());
        } else if (command.id.equals("MOVE_E")) {
            selection = 0;
            if (categoryLists != null)
                selectionCategory = cursorMove(selectionCategory, 1, categoryLists.size());
        } else if (command.id.equals("PASS")) {
            selectEntity();
        } else if (command.id.equals("ESC") && escapable) {
            escape();
        }
    }
    @Override
    public void mouseClick() { selectEntity(); }

    void selectEntity() {
        if (selectForVerbs) {
            UThing thing = (UThing)(shownEntities().get(selection));
            contextActions = thing.contextActions(commander.player());
            if (contextActions != null) {
                ArrayList<String> verbs = new ArrayList<>();
                for (String v : contextActions.keySet())
                    verbs.add(v);
                UModalStringPick smodal = new UModalStringPick(thing.getName() + ":", verbs.toArray(new String[verbs.size()]), this, "contextaction");
                smodal.setChildPosition(5,3+selection, this);
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
        dismissFrameEnd = 0;
        UAction action = contextActions.get(selection);
        if (action != null)
            commander.player().doAction(action);
    }
}
