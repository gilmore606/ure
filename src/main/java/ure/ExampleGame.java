package ure;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;


public class ExampleGame {

    static UREArea area;
    static URECamera camera;
    static URECommander commander;
    static UREActor player;

    private static JFrame makeWindow() {
        JFrame frame = new JFrame("Rogue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        camera = new URECamera(new URERenderer(), 1200, 800 , frame);
        camera.moveTo(area, 11,9);
        player.attachCamera(camera);
        camera.renderImage();
        frame.setSize(1000, 700);
        frame.add(camera);
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args)  {
        URETerrainCzar terrainCzar = new URETerrainCzar();
        area = new UREArea("/samplemap.txt", terrainCzar);
        URELight light = new URELight(Color.CYAN, 30);
        light.moveTo(area, 13,11);
        player = new UREActor("Player", '@', Color.WHITE, true);
        player.moveToCell(area, 11, 9);
        commander = new URECommander(player);
        makeWindow().addKeyListener(commander);

        commander.registerTimeListener(area);
        area.hearTick();
    }
}
