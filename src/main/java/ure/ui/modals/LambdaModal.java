package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;

import java.util.HashMap;

public class LambdaModal extends UModal {

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

    public void addCommand(char command, Lambda lambda) {
        commands.put(String.valueOf(command), lambda);
    }

    public void hearCommand(char command) {
        // Bypassing the normal headCommand in this case because we want the modal to be able to accept any characters.
        // You'd also want to do this if you were building a dialog where you wanted the user to enter a name or somesuch.
        Lambda lambda = commands.get(String.valueOf(command));
        if (lambda != null) {
            lambda.run();
            commander.detachModal();
        }
    }

    @Override
    public void draw(URenderer renderer) {
        renderer.drawRectBorder(0, 0, width, height, 1, UColor.COLOR_BLACK, UColor.COLOR_WHITE);
        renderer.drawString(5, 5, UColor.COLOR_WHITE, message);
    }
}
