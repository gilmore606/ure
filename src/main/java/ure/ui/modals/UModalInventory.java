package ure.ui.modals;

import ure.actors.actions.UAction;
import ure.things.UThing;
import ure.ui.modals.widgets.*;

import java.util.ArrayList;
import java.util.HashMap;

public class UModalInventory extends UModal implements HearModalDropdown {

    ArrayList<UThing> things;
    ArrayList<String> categories;
    ArrayList<ArrayList<UThing>> categoryLists;
    ArrayList<ArrayList<String>> categoryItemNames;
    int biggestCategoryLength;
    ArrayList<UAction> contextActions;

    WidgetSlideTabs categoryWidget;
    WidgetThingList listWidget;
    WidgetEntityDetail detailWidget;

    public UModalInventory(ArrayList<UThing> things) {
        super(null, "");
        this.things = things;
        Categorize();

        categoryWidget = new WidgetSlideTabs(this, 0, 0, 23, categories, 0);
        addWidget(categoryWidget);

        listWidget = new WidgetThingList(this, 0, 2, 15, 12);
        addWidget(listWidget);

        detailWidget = new WidgetEntityDetail(this, 15, 2);
        addWidget(detailWidget);

        sizeToWidgets();
        categoryWidget.select(0);
    }

    public void widgetChanged(Widget widget) {
        if (widget == categoryWidget) {
            changeList(categoryLists.get(categoryWidget.selection));
        } else if (widget == listWidget) {
            changeDetail(listWidget.thing());
        }
    }

    public void pressWidget(Widget widget) {
        if (widget == listWidget) {
            HashMap<String,UAction> actions = listWidget.thing().contextActions(commander.player());
            if (actions != null) {
                contextActions = new ArrayList<>();
                String[] verbs = new String[actions.size()];
                int i=0;
                for (String verb : actions.keySet()) {
                    verbs[i] = verb;
                    i++;
                    contextActions.add(actions.get(verb));
                }
                UModalDropdown drop = new UModalDropdown(verbs, 0, this, "verb");
                drop.setChildPosition(listWidget.x + 2, listWidget.y + listWidget.selection, this);
                commander.showModal(drop);
            }
        }
    }

    public void hearModalDropdown(String context, int selection) {
        dismiss();
        UAction action = contextActions.get(selection);
        if (action != null)
            commander.player().doAction(action);
    }

    void changeList(ArrayList<UThing> things) {
        listWidget.setThings(things);
    }
    void changeDetail(UThing thing) {
        detailWidget.setEntity(thing);
    }

    public void Categorize() {
        categories = new ArrayList<>();
        categoryLists = new ArrayList<>();
        categoryItemNames = new ArrayList<>();
        for (UThing thing : things) {
            String cat = thing.getCategory();
            if (categories.contains(cat)) {
                categoryLists.get(categories.indexOf(cat)).add(thing);
            } else {
                categories.add(cat);
                ArrayList<UThing> newList = new ArrayList<>();
                newList.add(thing);
                categoryLists.add(newList);
            }
        }
        int i = 0;
        for (ArrayList<UThing> theList : categoryLists) {
            if (theList.size() > biggestCategoryLength)
                biggestCategoryLength = theList.size();
            ArrayList<String> names = deDupeThings(theList);
            categoryItemNames.add(names);
            i++;
        }
    }

    public ArrayList<String> deDupeThings(ArrayList<UThing> sourceThings) {
        ArrayList<UThing> newent = new ArrayList<>();
        ArrayList<String> displaynames = new ArrayList<>();
        ArrayList<Integer> totals = new ArrayList<>();
        ArrayList<UThing> writeList = sourceThings;
        int i = 0;
        for (UThing thing : sourceThings) {
            boolean gotone = false;
            int ni = 0;
            for (UThing nent: newent) {
                if (nent.getName().equals(thing.getName())) {
                    gotone = true;
                } else if (!gotone) {
                    ni++;
                }
            }
            if (!gotone) {
                newent.add(thing);
                totals.add(1);
                i++;
            } else {
                totals.set(ni, totals.get(ni) + 1);
            }
        }
        sourceThings = newent;
        i = 0;
        writeList.clear();
        for (UThing thing : sourceThings) {
            int total = totals.get(i);
            if (total > 1)
                displaynames.add(Integer.toString(totals.get(i)) + " " + thing.getPlural());
            else
                displaynames.add(thing.name());
            i++;
            writeList.add(thing);
        }
        return displaynames;
    }
}
