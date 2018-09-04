package ure.ui.modals;

import ure.actors.actions.UAction;
import ure.things.UThing;
import ure.ui.modals.widgets.*;

import java.util.ArrayList;
import java.util.HashMap;

public class UModalInventory extends UModal implements HearModalDropdown {

    private ArrayList<UThing> things;
    private ArrayList<String> categories;
    private ArrayList<ArrayList<UThing>> categoryLists;
    private ArrayList<ArrayList<String>> categoryItemNames;
    int biggestCategoryLength;
    private ArrayList<UAction> contextActions;
    private String[] contextVerbs;

    private WidgetSlideTabs categoryWidget;
    private WidgetEntityList listWidget;
    private WidgetEntityDetail detailWidget;

    public UModalInventory(ArrayList<UThing> things) {
        super(null, "");
        this.things = things;
        Categorize();

        categoryWidget = new WidgetSlideTabs(this, 0, 0, 23, categories, 0);
        addWidget(categoryWidget);

        listWidget = new WidgetEntityList(this, 0, 2, 15, 12);
        addWidget(listWidget);

        detailWidget = new WidgetEntityDetail(this, 15, 2);
        addWidget(detailWidget);

        sizeToWidgets();
        categoryWidget.select(0);
        dismissFrameEnd = 0;
    }

    public void widgetChanged(Widget widget) {
        if (widget == categoryWidget) {
            listWidget.selection = 0;
            changeList(categoryLists.get(categoryWidget.selection));
            changeDetail((UThing)listWidget.entity());
        } else if (widget == listWidget) {
            changeDetail((UThing)listWidget.entity());
        }
    }

    public void pressWidget(Widget widget) {
        if (widget == listWidget && listWidget.entity() != null) {
            HashMap<String,UAction> actions = ((UThing)(listWidget.entity())).contextActions(commander.player());
            if (actions != null) {
                contextActions = new ArrayList<>();
                contextVerbs = new String[actions.size()];
                int i=0;
                for (String verb : actions.keySet()) {
                    contextVerbs[i] = verb;
                    i++;
                    contextActions.add(actions.get(verb));
                }
                UModalDropdown drop = new UModalDropdown(contextVerbs, 0, this, "verb");
                drop.setChildPosition(listWidget.col + 2, listWidget.row + listWidget.selection, this);
                commander.showModal(drop);
            }
        }
    }

    public void hearModalDropdown(String context, int selection) {
        UAction action = contextActions.get(selection);
        if (action != null) {
            commander.player().doAction(action);
        } else if (contextVerbs[selection].equals("add to hotbar")) {
            commander.player().addToHotbar((UThing)(listWidget.entity()));
        }
        reCategorize();
    }

    public void reCategorize() {
        things = commander.player().things();
        int oldcategories = categoryLists.size();
        Categorize();
        if (categoryLists.size() != oldcategories) {
            removeWidget(categoryWidget);
            categoryWidget = new WidgetSlideTabs(this, 0, 0, 23, categories, 0);
            addWidget(categoryWidget);
            categoryWidget.select(categoryLists.size() - 1);
        } else {
            categoryWidget.setTabs(categories);
        }
        changeList(categoryLists.get(categoryWidget.selection));
        changeDetail((UThing)listWidget.entity());
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
            if (!thing.equipped) {
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
        }
        int i = 0;
        for (ArrayList<UThing> theList : categoryLists) {
            if (theList.size() > biggestCategoryLength)
                biggestCategoryLength = theList.size();
            ArrayList<String> names = deDupeThings(theList);
            categoryItemNames.add(names);
            i++;
        }
        for (i=0;i<categories.size();i++) {
            categories.set(i, categories.get(i) + " (" + Integer.toString(categoryLists.get(i).size()) + ")");
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
