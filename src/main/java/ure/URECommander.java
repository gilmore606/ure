package ure;

import java.awt.event.KeyListener;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Receive input and dispatch game commands or UI controls.
 */


public class URECommander implements KeyListener {

    private HashMap<Character, String> keyBinds;
    private HashSet<UTimeListener> timeListeners;
    private HashSet<UAnimator> animators;

    private UREActor player;
    private UREScrollPanel scrollPrinter;
    private int turnCounter;
    private int turnsPerDay = 192;

    private int animationMillis = 50;

    public URECommander(UREActor theplayer) {
        timeListeners = new HashSet<UTimeListener>();
        animators = new HashSet<UAnimator>();
        setPlayer(theplayer);
        readKeyBinds();
        turnCounter = 0;
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
        keyBinds.put('w', "MOVE_N");
        keyBinds.put('s', "MOVE_S");
        keyBinds.put('a', "MOVE_W");
        keyBinds.put('d', "MOVE_E");
        keyBinds.put('e', "DEBUG");

    }

    public void keyPressed(KeyEvent e) {
        System.out.println("keypress " + Character.toString(e.getKeyChar()));
        char c = e.getKeyChar();
        if (keyBinds.containsKey((Character)c)) {
            hearCommand(keyBinds.get((Character)c));
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    void hearCommand(String command) {
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
            case "DEBUG":
                debug();
        }
        if (acted) {
            tickTime();
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

    void debug() {
        player.debug();
    }

    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

    void animationLoop() {
        try {
            Thread.sleep(animationMillis);
        } catch (InterruptedException e) {
            System.out.println("hi");
        }
        Iterator<UAnimator> animI = animators.iterator();
        while (animI.hasNext()) {
            animI.next().animationTick();
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
