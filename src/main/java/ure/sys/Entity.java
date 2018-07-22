package ure.sys;

import ure.ui.Icon;

import java.util.ArrayList;

/**
 * A game entity.  Entities have a name and an icon.  They can also have stats, which are simply named integer values.
 *
 */
public interface Entity {

    String getName();
    long getID();
    void setID(long newID);
    String getPlural();
    Icon getIcon();
    String getCategory();
    ArrayList<String> UIdetails(String context);
    void setStat(String attribute, int value);
    int getStat(String attribute);
}
