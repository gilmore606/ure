package ure.sys;

import ure.sys.UCommander;

/**
 * UTimeListener interface describes an object which wants to hear time ticks from the commander.
 *
 */
public interface UTimeListener {
    public void hearTimeTick(UCommander commander);
}
