package ure;

/**
 * UTimeListener interface describes an object which wants to hear time ticks from the commander.
 *
 */
public interface UTimeListener {
    public void hearTimeTick(UCommander commander);
}
