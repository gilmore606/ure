# Input

User keystrokes are detected through OpenGL (via URendererOGL) and made into GLKey objects, which store an int value and booleans
indicating whether CTRL or SHIFT were down for that keystroke.  The GLKey is then sent to UCommander for handling.

UCommander makes a table called keyBindings from its keybinds.txt file, mapping int GLKey values to named commands.  The values
correspond to constants GLFW_KEY_* (GLFW_KEY_A, GLFW_KEY_4, GLFW_KEY_SPACE, etc) which you can import via:
```
import static org.lwjgl.glfw.GLFW.*;
```
A complete list of these key values can be found [here](http://www.glfw.org/docs/latest/group__keys.html).

UCommander.consumeKeyFromBuffer() checks its keyBindings and converts the GLKey into a UCommand object, if it finds one that
matches.  UCommand has a subclass for every command in your game -- URE comes with many already made, such as CommandMove,
CommandGet, CommandInventory, etc.  These are tied to the keybinds.txt file through their String .id field.  To add a new
command, simply make a new CommandMycmd class, set its .id = "MYCMD", and bind MYCMD in keybinds.txt.

Once a Command is found for the key, UCommander.hearCommand() calls command.execute() to perform it.  However, if a Modal (a
blocking UI panel) is up, instead the command (if any) and GLKey are sent to the Modal for interpretation.  When writing
your own modals you can interpret the Commands or the raw GLKeys.  We recommend using the Commands when they're passed in, so
the user's possibly remapped movement/selection keys will work as she expects in your modal.

