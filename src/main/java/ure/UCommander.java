package ure;

import ure.actions.UActionGet;
import ure.actions.UActionPass;
import ure.actions.UActionWalk;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.areas.UCartographer;
import ure.math.UColor;
import ure.render.URenderer;
import ure.terrain.Stairs;
import ure.terrain.UTerrain;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.UModal;
import ure.ui.UScrollPanel;
import ure.ui.UREStatusPanel;
import ure.ui.USpeaker;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Receive input and dispatch game commands or UI controls.
 */


public class UCommander implements URenderer.KeyListener {

    private HashMap<Character, String> keyBinds;
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

    public void registerTimeListener(UTimeListener listener) {
        timeListeners.add(listener);
    }
    public void unregisterTimeListener(UTimeListener listener) {
        timeListeners.remove(listener);
    }

    public void registerActor(UActor actor) { actors.add(actor); }
    public void unregisterActor(UActor actor) { actors.remove(actor); }

    public void registerScrollPrinter(UScrollPanel printer) {
        scrollPrinter = printer;
    }


    public void addAnimator(UAnimator animator) { animators.add(animator); }
    public void removeAnimator(UAnimator animator) { animators.remove(animator); }

    public UActor player() { return player; }

    public void readKeyBinds() {
        // TODO: Actually read keybinds.txt
        //
        keyBinds = new HashMap<Character, String>();
        keyBinds.put('w', "MOVE_N");
        keyBinds.put('s', "MOVE_S");
        keyBinds.put('a', "MOVE_W");
        keyBinds.put('d', "MOVE_E");
        keyBinds.put('W', "LATCH_N");
        keyBinds.put('A', "LATCH_W");
        keyBinds.put('S', "LATCH_S");
        keyBinds.put('D', "LATCH_E");
        keyBinds.put(' ', "PASS");
        keyBinds.put('g', "GET");
        keyBinds.put('.', "STAIRS");
        keyBinds.put('i', "INVENTORY");
        keyBinds.put('e', "DEBUG");
        keyBinds.put('1', "DEBUG_1");
        keyBinds.put('2', "DEBUG_2");
        keyBinds.put('3', "DEBUG_3");
    }

    public void keyPressed(char c) {
        keyBuffer.add(c);
    }

    public void consumeKeyFromBuffer() {
        if (!keyBuffer.isEmpty()) {
            Character c = keyBuffer.remove();
            hearCommand(keyBinds.get(c));
        }
    }

    void hearCommand(String command) {
        if(command == null) return;
        System.out.println("actiontime " + Float.toString(player.actionTime()) + "   cmd: " + command);
        if (modal != null) {
            modal.hearCommand(command);
        } else {
            switch (command) {
                case "MOVE_N":
                    walkPlayer(0, -1);
                    break;
                case "MOVE_S":
                    walkPlayer(0, 1);
                    break;
                case "MOVE_W":
                    walkPlayer(-1, 0);
                    break;
                case "MOVE_E":
                    walkPlayer(1, 0);
                    break;
                case "LATCH_N":
                    latchPlayer(0,-1); break;
                case "LATCH_S":
                    latchPlayer(0,1); break;
                case "LATCH_W":
                    latchPlayer(-1,0); break;
                case "LATCH_E":
                    latchPlayer(1,0); break;
                case "PASS":
                    PassPlayer(); break;
                case "GET":
                    commandGet();
                    break;
                case "STAIRS":
                    commandStairs();
                    break;
                case "INVENTORY":
                    commandInventory();
                    break;
                case "DEBUG":
                    debug();
                    break;
                case "DEBUG_1":
                    debug_1();
                    break;
                case "DEBUG_2":
                    debug_2();
                    break;
                case "DEBUG_3":
                    debug_3();
                    break;
            }
        }
    }


    void walkPlayer(int xdir, int ydir) {
        player.doAction(new UActionWalk(player, xdir, ydir));
    }

    void latchPlayer(int xdir, int ydir) {
        moveLatch = true;
        moveLatchX = xdir;
        moveLatchY = ydir;
        walkPlayer(xdir, ydir);
    }

   public void latchBreak() {
        moveLatch = false;
        moveLatchX = 0;
        moveLatchY = 0;
    }

    void PassPlayer() {
        player.doAction(new UActionPass(player));
    }

    void commandGet() {
        player.doAction(new UActionGet(player));
    }

    void commandStairs() {
        UTerrain t = player.area().terrainAt(player.areaX(), player.areaY());
        if (t instanceof Stairs) {
            ((Stairs)t).transportActor(player);
        } else {
            printScroll("You don't see anything to move through.");
        }
    }

    void commandInventory() {
        UModal modal = makeInventoryModal();
        showModal(modal);
    }

    UModal makeInventoryModal() {
        UModal modal = new UModal(30,30, renderer, player.camera, UColor.COLOR_BLACK);
        Iterator<UThing> things = player.iterator();
        int i = 1;
        while (things.hasNext()) {
            UThing thing = things.next();
            modal.addText("item" + Integer.toString(i), thing.name(), 2, i + 1);
            // TODO: figure out what to do with the color here
            //modal.addText("item" + Integer.toString(i), thing.name, 2, i + 1, renderer.UItextColor.makeAWTColor());
            i++;
        }
        return modal;
    }

    void showModal(UModal modal) {
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

    void debug_3() { }

    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

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
                            walkPlayer(moveLatchX, moveLatchY);
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

    public void attachModal(UModal modal) {
        if (this.modal != null) {
            this.detachModal();
        }
        this.modal = modal;
    }

    public void detachModal() {
        this.modal = null;
    }

}
