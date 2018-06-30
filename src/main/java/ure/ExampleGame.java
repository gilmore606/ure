package ure;

import ure.terrain.URETerrainCzar;

import java.awt.*;
import javax.swing.*;


public class ExampleGame implements UTimeListener {

    static UREArea area;
    static URECamera camera;
    static URECommander commander;
    static UREActor player;
    static JFrame frame;
    static UREStatusPanel statusPanel;
    static UREScrollPanel scrollPanel;
    static Font font;

    private JFrame makeWindow() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/Px437_Phoenix_BIOS-2y.ttf")).deriveFont(Font.PLAIN, 16);
        } catch (Exception e) {
            System.out.println("Failed to load font");
        }
        //font = new Font("Px437 Verite 9x14", Font.PLAIN, 16);

        frame = new JFrame("Rogue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.getContentPane().setLayout(null);
        frame.setBounds(0,0,1400,1000);

        camera = new URECamera(new URERenderer(font), 1200, 800 , frame);
        camera.moveTo(area, 40,20);
        player.attachCamera(camera, URECamera.PINSTYLE_SOFT);
        camera.setBounds(0,0,1200,800);
        camera.renderImage();

        statusPanel = new UREStatusPanel(font, 15, 10, 16, 16, 10, 10, new UColor(1f,1f,1f), new UColor(0f,0f,0f));
        statusPanel.addText("name", "Player 1",0,0);
        statusPanel.addText("race", "Elf",0,1);
        statusPanel.addText("class", "Homo",0,2);
        statusPanel.addText("turn", "T 1", 0, 5);
        statusPanel.addText("time", "", 0, 6);
        statusPanel.setBounds(1200,0,200,800);

        scrollPanel = new UREScrollPanel(font, 5, 80, 16, 16, 5, 5, new UColor(1f,1f,1f), new UColor(0f,0f,0f));
        scrollPanel.addLineFade(Color.WHITE);
        scrollPanel.addLineFade(new Color(0.6f, 0.6f, 0.6f));
        scrollPanel.addLineFade(new Color(0.4f, 0.4f, 0.4f));
        scrollPanel.addLineFade(new Color(0.3f, 0.3f, 0.3f));
        scrollPanel.setBounds(0,800,1400,200);
        scrollPanel.print("Welcome to UnRogueEngine!");
        scrollPanel.print("The universal java toolkit for roguelike games.");
        scrollPanel.print("Your journey begins...");


        frame.getContentPane().add(statusPanel);
        frame.getContentPane().add(scrollPanel);
        frame.getContentPane().add(camera);

        frame.setLocationRelativeTo(null);
        frame.setSize(1400, 1000);
        frame.getContentPane().setFocusable(true);
        frame.setVisible(true);
        frame.getContentPane().requestFocusInWindow();
        return frame;
    }

    public void startUp()  {
        URETerrainCzar terrainCzar = new URETerrainCzar();
        terrainCzar.loadTerrains("/terrains.json");
        //area = new UREArea("/samplemap.txt", terrainCzar);
        area = new UREArea(100, 100, terrainCzar, "wall");
        URELandscaper scaper = new URELandscaper();
        scaper.digCaves(area, "floor",2, 2, 98, 98);
        scaper.digRiver(area, "water", 0, 0, 99, 99, 5f, 0.7f, 1.4f);
        scaper.digRiver(area, "water", 0, 0, 99, 99, 3f, 0.9f, 2f);
        player = new UREPlayer("Player", '@', new UColor(Color.WHITE), true, 4, 6);
        int px = 20;
        int py = 20;
        for (int x=30;x<80;x++) {
            for (int y=30;y<80;y++) {
                if (area.willAcceptThing(player, x, y))
                    px = x; py = y;
            }
        }
        player.moveToCell(area, px, py);
        commander = new URECommander(player);
        area.setCommander(commander);
        makeWindow().getContentPane().addKeyListener(commander);

        commander.registerScrollPrinter(scrollPanel);
        commander.registerTimeListener(area);
        commander.registerTimeListener(this);
        commander.addAnimator(camera);

        commander.gameLoop(frame);
    }

    public void hearTick(URECommander commander) {

        statusPanel.setText("turn", "T " + Integer.toString(commander.getTurn()));
        statusPanel.setText("time", commander.timeString(true, " "));
    }
}
