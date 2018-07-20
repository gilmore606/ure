After your game does its initial setup, splash screen, title menu etc, it hands control to UCommander to run the main
game loop:
```
commander.gameLoop();
```
This method loops forever (hence the name) waiting for user input, updating and redrawing the display, and advancing the
world simulation.  It also controls when actors (including the player) get to act and how often.

gameLoop tries to maintain a redraw rate of UConfig.FPSrate; it will attempt to re-render the display this often.  Every
UConfig.animFrameMilliseconds ms it will call commander.animationFrame(), which fans out to animation-aware entities.

## actionTime and ticks

Actor actions are handled by gameLoop using a parameter on all actors (including the player) called actionTime.  Every actor
has a store of actionTime which it can spend to perform actions.  Every 'tick' of the game clock, 1.0 actionTime is added
to every actor.  All non-player actors are polled sequentially to let them spend their actionTime -- as they execute [Actions](doc/Actions.md),
those actions return a time cost.  When an actor has 0 actionTime, he's done and the next actor then acts.

When all non-player actors have been given a chance to spend their actionTime and none can act, and the player has actionTime
to spend, the gameLoop switches to its
waitingForInput mode, stopping the gameworld until the player executes a command.  The player also spends actionTime to perform
her commands, and can continue to do so until she also has <=0 actionTime, at which point NPC actors are given their turn and
the cycle continues.  Every 1.0 action time that passes also causes commander.tickTime() which advances the gameworld clock
by one turn.

The actionTime cost of URE's built-in actions can vary based on many factors -- Actors can have a faster or slower actionSpeed
which affects all action costs, or a faster or slower moveSpeed which affects only the cost of movement.  Terrain may modify
movement costs so rough terrain costs more time, or roads cost less.  If you don't want this behavior in your game you can
simply set these entities' variables to a constant 1.0.

