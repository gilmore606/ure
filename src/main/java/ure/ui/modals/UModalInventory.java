package ure.ui.modals;

import ure.things.UThing;
import ure.ui.modals.widgets.WidgetEntityDetail;
import ure.ui.modals.widgets.WidgetListVert;
import ure.ui.modals.widgets.WidgetSlideTabs;

import java.util.ArrayList;

public class UModalInventory extends UModal {

    ArrayList<UThing> things;
    ArrayList<String> categories;
    ArrayList<ArrayList<UThing>> categoryLists;
    ArrayList<ArrayList<String>> categoryItemNames;
    int biggestCategoryLength;

    WidgetSlideTabs categoryWidget;
    WidgetListVert listWidget;
    WidgetEntityDetail detailWidget;

    public UModalInventory(ArrayList<UThing> things) {
        super(null, "");
        this.things = things;
        Categorize();

        categoryWidget = new WidgetSlideTabs(this, 0, 0, 20, categories, 0);
        addWidget(categoryWidget);

        sizeToWidgets();
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
