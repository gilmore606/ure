package ure.sys;

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
import ure.ui.UCamera;
import ure.ui.modals.UModal;
import ure.ui.UScrollPanel;
import ure.ui.UStatusPanel;
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

    public UConfig config;

    private HashMap<Character, UCommand> keyBindings;
    private HashSet<UTimeListener> timeListeners;
    private HashSet<UAnimator> animators;
    private ArrayList<UActor> actors;

    private URenderer renderer;
    private UActor player;
    private UScrollPanel scrollPrinter;
    private UCamera modalCamera;

    private UScrollPanel scrollPanel;
    private UStatusPanel statusPanel;

    public UThingCzar thingCzar;
    public UActorCzar actorCzar;
    public UCartographer cartographer;
    public USpeaker speaker;

    public int turnCounter = 0;
    public int frameCounter;

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
        config = new UConfig();
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
     */
    public void registerActor(UActor actor) { actors.add(actor); }
    public void unregisterActor(UActor actor) { actors.remove(actor); }

    /**
     * Register a UI component to print scroll messages.  Right now this can only be a UScrollPrinter.
     *
     */
    public void registerScrollPrinter(UScrollPanel printer) {
        scrollPrinter = printer;
    }

    /**
     * Register the camera to center modals on (if UConfig.modalPosition = POS_CAMERA_CENTER).
     *
     */
    public void registerModalCamera(UCamera camera) { this.modalCamera = camera; }
    public UCamera modalCamera() { return modalCamera; }

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
        keyBindings.put('i', new CommandInventory());
        keyBindings.put('g', new CommandGet());
        keyBindings.put('.', new CommandTravel());
    }

    public void keyPressed(char c) {
        keyBuffer.add(c);
    }

    public int mouseX() { return renderer.getMousePosX(); }
    public int mouseY() { return renderer.getMousePosY(); }

    public void consumeKeyFromBuffer() {
        if (!keyBuffer.isEmpty()) {
            Character c = keyBuffer.remove();
            UCommand command = keyBindings.get(c);
            if (command != null) {
                hearCommand(keyBindings.get(c), c);
            } else if (c == '1') {
                debug_1();
            } else if (c == '2') {
                debug_2();
            } else if (c == 'q') {
                debug();
            }
        }
    }

    void hearCommand(UCommand command, Character c) {
        System.out.println("actiontime " + Float.toString(player.actionTime()) + "   cmd: " + command.id);
        if (modal != null) {
            System.out.println("sent " + command.id + " to modal");
            modal.hearCommand(command, c);
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
        config.setVisibilityEnable(!config.isVisibilityEnable());
    }

    void debug_2() {
        config.setLightEnable(!config.isLightEnable());
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
        if (modal != null)
            modal.animationTick();
    }

    public void setStatusPanel(UStatusPanel panel){
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
        long tickRate = 1000000000 / config.getFPStarget();
        long gameTime = System.nanoTime();
        while (!renderer.windowShouldClose()) {
            frameCounter++;
            renderer.pollEvents();

            //Finalize and flush what we've rendered above to screen.
            renderer.render();

            long curTime = System.nanoTime();
            if (curTime - gameTime > config.getAnimFrameMilliseconds() * 1000)
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
        return (int)(((float)((turnCounter + config.getDayTimeStartOffset()) % config.getTurnsPerDay()) / (float)config.getTurnsPerDay()) * 1440f);
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
