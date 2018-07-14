package ure.sys;

import ure.ui.Icon;

/**
 * A game entity.
 *
 */
public interface Entity {

    String getName();
    Icon getIcon();
    String[] UIdetails(String context);
}
