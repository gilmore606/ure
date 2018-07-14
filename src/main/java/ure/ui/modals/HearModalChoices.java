package ure.ui.modals;

/**
 * Implement this interface to receive callbacks from a ModalChoices.
 *
 */
public interface HearModalChoices extends HearModal {

    void hearModalChoices(String context, String choice);

}
