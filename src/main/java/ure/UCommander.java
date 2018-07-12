package ure;

import ure.actions.ActionWalk;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.commands.*;
import ure.commands.UCommand;
import ure.render.URenderer;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.modals.LambdaModal;
import ure.ui.modals.UModal;
import ure.ui.UScrollPanel;
import ure.ui.UREStatusPanel;
import ure.ui.USpeaker;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * UCommander is a singleton class (you only make one of these!) which receives player input, converts it to
 * commands, runs the game loop, tracks NPC/player action time, and tracks world turn/time.  Think of it as the
 * central control hub of a URE game.
 *
 */


public class UCommander implements URenderer.KeyListener {

    private HashMap<Character, UCommand> keyBindings;
    private HashSet<UTimeListener> timeListeners;
    private HashSet<UAnimator> animators;
    private ArrayList<UActor> actors;

    private URenderer renderer;
    private UActor player;
    private UScrollPanel scrollPrinter;

    private UScrollPanel scrollPanel;
    private UREStatusPanel statusPanel;

    public UThingCzar thingCzar;
    public UActorCzar actorCzar;
    public UCartographer cartographer;
    public USpeaker speaker;

    public int turnCounter = 0;
    private int turnsPerDay = 512;
    public int frameCounter;

    private int animationMillis = 33;

    private boolean breakLatchOnInput = true;

    private LinkedBlockingQueue<Character> keyBuffer;
    private int keyBufferSize = 2;

    private boolean waitingForInput = false;
    private boolean moveLatch = false;
    private int moveLatchX = 0;
    private int moveLatchY = 0;

    private UModal modal;

    public UCommander() {
        Injector.getAppComponent().inject(this);
    }
    public void registerComponents(UActor theplayer, URenderer theRenderer, UThingCzar thingczar, UActorCzar actorczar, UCartographer carto) {
        renderer = theRenderer;
        timeListeners = new HashSet<UTimeListener>();
        animators = new HashSet<UAnimator>();
        actors = new ArrayList<UActor>();
        thingCzar = thingczar;
        actorCzar = actorczar;
        cartographer = carto;

        setPlayer(theplayer);
        readKeyBinds();
        renderer.setKeyListener(this);
        keyBuffer = new LinkedBlockingQueue<Character>();
        speaker = new USpeaker();
    }

    public int getTurn() { return turnCounter; };

    public void setPlayer(UActor theplayer) {
        player = theplayer;
    }

    /**
     * Any object which implements UTimeListener can register with this method to have its hearTimeTick() called
     * on every game tick.
     *
     * @param listener
     */
    public void registerTimeListener(UTimeListener listener) {
        timeListeners.add(listener);
    }
    public void unregisterTimeListener(UTimeListener listener) {
        timeListeners.remove(listener);
    }

    /**
     * Newly spawned actors must register with the commander to get action time and thereby...act.
     *
     * @param actor
     */
    public void registerActor(UActor actor) { actors.add(actor); }
    public void unregisterActor(UActor actor) { actors.remove(actor); }

    /**
     * Register a UI component to print scroll messages.  Right now this can only be a UScrollPrinter.
     *
     * @param printer
     */
    public void registerScrollPrinter(UScrollPanel printer) {
        scrollPrinter = printer;
    }


    public void addAnimator(UAnimator animator) { animators.add(animator); }
    public void removeAnimator(UAnimator animator) { animators.remove(animator); }

    public UActor player() { return player; }

    /**
     * Read keybinds.txt and map keys to commands.
     *
     * Commands are subclasses of UCommand; use the 'id' string of the particular subclass in the keybinds.txt file.
     *
     */
    public void readKeyBinds() {
        // TODO: Actually read keybinds.txt
        //
        keyBindings = new HashMap<Character, UCommand>();
        keyBindings.put('w', new CommandMoveN());
        keyBindings.put('s', new CommandMoveS());
        keyBindings.put('a', new CommandMoveW());
        keyBindings.put('d', new CommandMoveE());
        keyBindings.put('W', new CommandLatchN());
        keyBindings.put('S', new CommandLatchS());
        keyBindings.put('A', new CommandLatchW());
        keyBindings.put('D', new CommandLatchE());
        keyBindings.put(' ', new CommandPass());
        keyBindings.put('e', new CommandInteract());
        keyBindings.put('g', new CommandGet());
        keyBindings.put('.', new CommandTravel());
    }

    public void keyPressed(char c) {
        keyBuffer.add(c);
    }

    public void consumeKeyFromBuffer() {
        if (!keyBuffer.isEmpty()) {
            Character c = keyBuffer.remove();

            if (modal instanceof LambdaModal) {
                // We want the this kind of modal to be able to process any character if it's active.
                // This will allow processing any kind of raw text (entering a name for something, etc).
                ((LambdaModal) modal).hearCommand(c);
                return;
            }

            UCommand command = keyBindings.get(c);
            if (command != null) {
                hearCommand(keyBindings.get(c));
            } else if (c == '1') {
                debug_1();
            } else if (c == '2') {
                debug_2();
            } else if (c == 'q') {
                debug();
            } else if (c == '3') {
                debug_3();
            }
        }
    }

    void hearCommand(UCommand command) {
        System.out.println("actiontime " + Float.toString(player.actionTime()) + "   cmd: " + command.id);
        if (modal != null) {
            modal.hearCommand(command);
        } else {
            command.execute((UPlayer)player);
        }
    }

    public void setMoveLatch(int xdir, int ydir) {
        moveLatch = true;
        moveLatchX = xdir;
        moveLatchY = ydir;
    }

    /**
     * Cancel player latched auto-movement, if engaged.
     * Anything in the world that might be interesting during auto-movement should call this if the player approaches.
     *
     */
    public void latchBreak() {
        moveLatch = false;
        moveLatchX = 0;
        moveLatchY = 0;
    }

    /**
     * Show a UModal dialog and send all player input to it.
     *
     * @param modal
     */
    public void showModal(UModal modal) {
        attachModal(modal);
    }

    void debug() {
        player.debug();
    }

    void debug_1() {
        player.camera.setAllVisible(!player.camera.getAllVisible());
    }

    void debug_2() {
        player.camera.setAllLit(!player.camera.getAllLit());
    }

    void debug_3() {
        LambdaModal modal = new LambdaModal("This is only a test.  Do you like turtles? (y/n)");
        modal.addCommand('y', () -> {
            this.printScroll("That's awesome, I like turtles too!");
        });
        modal.addCommand('n', () -> {
            this.printScroll("That's too bad.  Turtles rule.");
        });
        attachModal(modal);
    }

    /**
     * Print a message to the scroll printer.
     *
     * @param text
     */
    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

    /**
     * Print a message to the scroll printer if the player can see the source.
     *
     * @param source
     * @param text
     */
    public void printScrollIfSeen(UThing source, String text) {
        if (player.canSee(source))
            printScroll(text);
    }

    void animationFrame() {
        for (UAnimator anim : animators) {
            anim.animationTick();
        }
    }

    public void setStatusPanel(UREStatusPanel panel){
        statusPanel = panel;
    }

    public void setScrollPanel(UScrollPanel panel){
        scrollPanel = panel;
    }

    public void playerChangedArea(UArea sourcearea, UArea destarea) {
        statusPanel.setText("location", cartographer.describeLabel(destarea.label));
    }
    /**
     * The gameLoop() runs forever (until the player exits the game).  It should be the last thing you call from
     * your game's main() after everything is initialized.
     *
     */
    public void gameLoop() {
        long tickRate = 1000000000 / 60;
        long gameTime = System.nanoTime();
        while (!renderer.windowShouldClose()) {
            frameCounter++;
            renderer.pollEvents();

            //Finalize and flush what we've rendered above to screen.
            renderer.render();

            long curTime = System.nanoTime();
            if (curTime - gameTime > animationMillis * 1000)
                animationFrame();
            if (curTime > gameTime + tickRate * 2) gameTime = curTime;
            else gameTime += tickRate;
            while (System.nanoTime() < gameTime) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }

            if (!waitingForInput) {
                for (UActor actor : actors) {
                    actor.act();
                }
                waitingForInput = true;
            }
            // if it's the player's turn, do a command if we have one
            if (waitingForInput) {
                if (!keyBuffer.isEmpty() || moveLatch) {
                    if (moveLatch) {
                        if (breakLatchOnInput && !keyBuffer.isEmpty()) {
                            latchBreak();
                            consumeKeyFromBuffer();
                        } else {
                            player.doAction(new ActionWalk(player, moveLatchX, moveLatchY));
                        }
                    } else {
                        consumeKeyFromBuffer();
                    }
                    renderer.render();
                    while (player.actionTime() <= 0f) {
                        tickTime();
                        waitingForInput = false;
                    }
                }
            }
        }
    }

    public void tickTime() {
        for (UActor actor : actors) {
            actor.addActionTime(1f);
        }
        Iterator<UTimeListener> timeI = timeListeners.iterator();
        while (timeI.hasNext()) {
            timeI.next().hearTimeTick(this);
        }
        turnCounter++;
        System.out.println("time:tick " + Integer.toString(turnCounter));
        renderer.render();
    }

    public int daytimeMinutes() {
        return (int)(((float)(turnCounter % turnsPerDay) / (float)turnsPerDay) * 1440f);
    }
    public int daytimeMM() {
        return (daytimeMinutes() % 60);
    }
    public int daytimeHH() {
        return daytimeMinutes() / 60 + 1;
    }
    public String timeString(boolean ampm, String zeropad) {
        String t = "";
        int h = daytimeHH();
        int m = daytimeMM();
        boolean pm = false;
        if (ampm) {
            if (h > 12) {
                h = h - 12;
                pm = true;
            }
            if (h < 10)
                t = t + zeropad;
            t = t + Integer.toString(h);
        }
        t = t + ":";
        if (m < 10)
            t = t + "0";
        t = t + Integer.toString(m);
        if (ampm) {
            if (pm) {
                t = t + "pm";
            } else {
                t = t + "am";
            }
        }
        return t;
    }

    void attachModal(UModal newmodal) {
        if (modal != null) {
            detachModal();
        }
        modal = newmodal;
        renderer.getRootView().addChild(modal);
    }

    public void detachModal() {
        renderer.getRootView().removeChild(modal);
        this.modal = null;
    }

}
