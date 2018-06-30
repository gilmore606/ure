package ure;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * Receive input and dispatch game commands or UI controls.
 */


public class URECommander implements KeyListener {

    private HashMap<Character, String> keyBinds;
    private HashSet<UTimeListener> timeListeners;
    private HashSet<UAnimator> animators;
    private URERendererOGL renderer;
    private UREActor player;
    private UREScrollPanel scrollPrinter;

    private UREScrollPanel scrollPanel;
    private UREStatusPanel statusPanel;

    private int turnCounter;
    private int turnsPerDay = 512;

    private int animationMillis = 33;

    private LinkedBlockingQueue<Character> keyBuffer;
    private int keyBufferSize = 2;

    public URECommander(UREActor theplayer, URERendererOGL theRenderer) {
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
        keyBinds.put('E', "DEBUG");

    }

    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (keyBinds.containsKey((Character)c)) {
            //hearCommand(keyBinds.get((Character)c));
            if (keyBuffer.size() < keyBufferSize)
                keyBuffer.add((Character)c);
        }
    }
    public void keyPressed(char c) {

        hearCommand(keyBinds.get(c));
        /*if (keyBinds.containsKey((Character)c)) {
            //hearCommand(keyBinds.get((Character)c));
            if (keyBuffer.size() < keyBufferSize)
                keyBuffer.add((Character)c);
        }*/
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

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
                case "DEBUG":
                    debug();
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
        UIModal modal = new UIModal(20,10, renderer, player.camera, UColor.COLOR_BLACK);
        showModal(modal);
    }

    void showModal(UIModal modal) {
        player.camera.attachModal(modal);
    }

    void debug() {
        player.debug();
    }

    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

    void animationFrame() {
        for (UAnimator anim : animators) {
            anim.animationTick();
        }
        player.camera.paintFrameBuffer(); // TODO: make this more generic and notify all cameras
    }

    void setStatusPanel(UREStatusPanel panel){
        statusPanel = panel;
    }

    void setScrollPanel(UREScrollPanel panel){
        scrollPanel = panel;
    }

    public void gameLoop() {
        long tickRate = 1000000000 / 3000000;
        long gameTime = System.nanoTime();
        while (!glfwWindowShouldClose(renderer.window)) {
            glfwPollEvents();


            renderer.renderCamera(player.camera);
            scrollPanel.renderImage();
            statusPanel.renderImage();
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
