package ure.actors.actions;

import ure.actors.UActor;

/**
 * The Interactable interface is shared by Terrain and Thing to control what can be interacted with using the
 * 'interact' action, by whom, and what it does when interacted with.
 *
 */
public interface Interactable {

    boolean isInteractable(UActor actor);
    float interactionFrom(UActor actor);

}
