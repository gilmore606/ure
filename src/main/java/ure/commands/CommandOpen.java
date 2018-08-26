package ure.commands;

public class CommandOpen extends CommandUseVerb {

    public static final String id = "OPEN";

    public CommandOpen() {
        super(id);
        verb = "open";
        noTargetsMsg = "You don't see anything to open.";
        whichDialog = "Open what?\n ";
        useOnGround = true;
    }
}
