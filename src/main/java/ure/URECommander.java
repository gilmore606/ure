package ure;

import ure.actors.UREActor;
import ure.render.URERenderer;
import ure.things.UREThing;
import ure.ui.UIModal;
import ure.ui.UREScrollPanel;
import ure.ui.UREStatusPanel;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Receive input and dispatch game commands or UI controls.
 */


public class URECommander {

    private HashMap<Character, String> keyBinds;
    private HashSet<UTimeListener> timeListeners;
    private HashSet<UAnimator> animators;
    private URERenderer renderer;
    private UREActor player;
    private UREScrollPanel scrollPrinter;

    private UREScrollPanel scrollPanel;
    private UREStatusPanel statusPanel;

    private int turnCounter;
    private int turnsPerDay = 512;

    private int animationMillis = 33;

    private LinkedBlockingQueue<Character> keyBuffer;
    private int keyBufferSize = 2;

    public URECommander(UREActor theplayer, URERenderer theRenderer) {
        renderer = theRenderer;
        timeListeners = new HashSet<UTimeListener>();
        animators = new HashSet<UAnimator>();
        setPlayer(theplayer);
        readKeyBinds();
        turnCounter = 0;
        keyBuffer = new LinkedBlockingQueue<Character>();
    }

    public int getTurn() { return turnCounter; };

    public void setPlayer(UREActor theplayer) {
        player = theplayer;
    }

    public void registerTimeListener(UTimeListener listener) {
        timeListeners.add(listener);
    }

    public void unRegisterTimeListener(UTimeListener listener) {
        timeListeners.remove(listener);
    }

    public void registerScrollPrinter(UREScrollPanel printer) {
        scrollPrinter = printer;
    }

    public void addAnimator(UAnimator animator) { animators.add(animator); }
    public void removeAnimator(UAnimator animator) { animators.remove(animator); }

    public UREActor player() { return player; }

    public void readKeyBinds() {
        // TODO: Actually read keybinds.txt
        //
        keyBinds = new HashMap<Character, String>();
        keyBinds.put('W', "MOVE_N");
        keyBinds.put('S', "MOVE_S");
        keyBinds.put('A', "MOVE_W");
        keyBinds.put('D', "MOVE_E");
        keyBinds.put('G', "GET");
        keyBinds.put('I', "INVENTORY");
        keyBinds.put('E', "DEBUG");
        keyBinds.put('1', "DEBUG_1");
        keyBinds.put('2', "DEBUG_2");
        keyBinds.put('3', "DEBUG_3");
    }

    public void keyPressed(char c) {
        hearCommand(keyBinds.get(c));
    }

    public void consumeKeyFromBuffer() {
        if (!keyBuffer.isEmpty()) {
            Character c = keyBuffer.remove();
            hearCommand(keyBinds.get(c));
        }
    }

    void hearCommand(String command) {
        if(command == null) return;
        System.out.println("cmd: " + command);
        if (player.camera.modal != null) {
            player.camera.modal.hearCommand(command);
        } else {
            boolean acted = true;
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
                case "GET":
                    commandGet();
                    break;
                case "INVENTORY":
                    commandInventory();
                    break;
                case "DEBUG":
                    debug();
                    break;
                case "DEBUG_1":
                    debug_1();
                    acted = false;
                    break;
                case "DEBUG_2":
                    debug_2();
                    acted = false;
                    break;
                case "DEBUG_3":
                    debug_3();
                    acted = false;
                    break;

            }
            if (acted) {
                tickTime();
            }
        }
    }

    public void tickTime() {
        Iterator<UTimeListener> timeI = timeListeners.iterator();
        while (timeI.hasNext()) {
            timeI.next().hearTick(this);
        }
        turnCounter++;
        System.out.println("tick");
        //System.gc();
    }

    void walkPlayer(int xdir, int ydir) {
        player.walkDir(xdir,ydir);
    }

    void commandGet() {
        //UIModal modal = new UIModal(20,10, renderer, player.camera, UColor.COLOR_BLACK);
        //UIModal modal = UIModal.popMessage("Nothing happened.", renderer, player.camera, UColor.COLOR_BLACK);
        //showModal(modal);
        if (player.myCell() != null)
            player.tryGetThing(player.myCell().topThingAt());
    }

    void commandInventory() {
        UIModal modal = makeInventoryModal();
        showModal(modal);
    }

    UIModal makeInventoryModal() {
        UIModal modal = new UIModal(30,30, renderer, player.camera, UColor.COLOR_BLACK);
        Iterator<UREThing> things = player.iterator();
        int i = 1;
        while (things.hasNext()) {
            UREThing thing = things.next();
            modal.addText("item" + Integer.toString(i), thing.name, 2, i + 1);
            // TODO: figure out what to do with the color here
            //modal.addText("item" + Integer.toString(i), thing.name, 2, i + 1, renderer.UItextColor.makeAWTColor());
            i++;
        }
        return modal;
    }

    void showModal(UIModal modal) {
        player.camera.attachModal(modal);
    }

    void debug() {
        player.debug();
    }

    void debug_1() {
        player.camera.setAllVisible(!player.camera.allVisible);
    }

    void debug_2() {
        player.camera.setAllLit(!player.camera.allLit);
    }

    void debug_3() { }

    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

    public void printScrollIfSeen(UREThing source, String text) {
        if (player.canSee(source))
            printScroll(text);
    }

    void animationFrame() {
        for (UAnimator anim : animators) {
            anim.animationTick();
        }
        //player.camera.paintFrameBuffer(); // TODO: make this more generic and notify all cameras
    }

    void setStatusPanel(UREStatusPanel panel){
        statusPanel = panel;
    }

    void setScrollPanel(UREScrollPanel panel){
        scrollPanel = panel;
    }

    public void gameLoop() {
        long tickRate = 1000000000 / 60;
        long gameTime = System.nanoTime();
        while (!renderer.windowShouldClose()) {
            renderer.pollEvents();
            renderer.drawCamera(player.camera);
            scrollPanel.renderImage();
            statusPanel.renderImage();


            //Finalize and flush what we've rendered above to screen.
            renderer.render();
            long curTime = System.nanoTime();
            if (curTime - gameTime > animationMillis*1000)
                animationFrame();
            if (curTime > gameTime + tickRate * 2) gameTime = curTime;
            else gameTime += tickRate;
            while (System.nanoTime() < gameTime) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) { }
            }
            if (!keyBuffer.isEmpty()) {
                consumeKeyFromBuffer();
                //frame.repaint();
            }
        }
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
}
