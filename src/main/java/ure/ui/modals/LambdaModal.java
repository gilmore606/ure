package ure.ui.modals;

import ure.math.UColor;
import ure.render.URenderer;

import java.util.HashMap;

public class LambdaModal extends UModal {

    // This interface is what allows us to store the lambda function.  The annotation is just a safety thing.  It
    // doesn't do anything except make our intentions clear to the compiler.  Interfaces for functions can only
    // have one method, so if we're using this annotation it would error out if tried to add something else to the
    // interface.  The name of the method is arbitrary, but if the lambda took any arguments we'd need to specify
    // them as parameters here.
    @FunctionalInterface
    public interface Lambda {
        void run();
    }

    private String message;

    private HashMap<String, Lambda> commands = new HashMap<>();

    public LambdaModal(String message) {
        this.message = message;
        this.x = 100;
        this.y = 100;
        this.width = 500;
        this.height = 200;
    }

    public void addKeyMapping(char key, Lambda lambda) {
        // Store the lambda for this key referenced by the key press we're mapping it to.
        // We can't use primitive types in the hash so we convert the key to a string.
        commands.put(String.valueOf(key), lambda);
    }

    public void hearKeyPress(char key) {
        // Bypassing the normal hearCommand in this case because we want the modal to be able to accept any characters
        // and not try to figure out what key press a passed command represents.
        // You'd probably also want to do this if you were building a dialog where you wanted the user to enter a name
        // or some other data.
        Lambda lambda = commands.get(String.valueOf(key));
        if (lambda != null) {
            // We have a mapping for this key.  Run the lambda function associated with it.
            lambda.run();
            commander.detachModal();
        }
        // Note that we don't detach unless we get one of our expected key presses.
    }

    @Override
    public void draw(URenderer renderer) {
        renderer.drawRectBorder(0, 0, width, height, 1, UColor.COLOR_BLACK, UColor.COLOR_WHITE);
        renderer.drawString(5, 5, UColor.COLOR_WHITE, message);
    }
}
