package ure.sys;

import ure.areas.UArea;
import ure.things.UContainer;
import ure.ui.Icons.Icon;

import java.util.ArrayList;

/**
 * A game entity.  Entities have a name and an icon.  They can also have stats, which are simply named integer values.
 *
 */
public interface Entity {

    String getName();
    String name();
    long getID();
    void setID(long newID);
    Icon icon();
    UContainer location();
    String getPlural();
    String getCategory();
    ArrayList<String> UIdetails(String context);
    ArrayList<String> UItips(String context);
    void setStat(String attribute, int value);
    int getStat(String attribute);
    UArea area();
    int areaX();
    int areaY();
}
