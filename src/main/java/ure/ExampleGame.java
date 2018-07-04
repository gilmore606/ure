package ure;

import ure.actors.UREActor;
import ure.actors.UREActorCzar;
import ure.actors.UREPlayer;
import ure.render.URERenderer;
import ure.render.URERendererOGL;
import ure.terrain.URETerrainCzar;
import ure.things.UREThingCzar;
import ure.ui.UREScrollPanel;
import ure.ui.UREStatusPanel;

import java.awt.*;

public class ExampleGame implements UTimeListener {

    static UREArea area;
    static URECamera camera;
    static URECommander commander;
    static UREActor player;
    static UREStatusPanel statusPanel;
    static UREScrollPanel scrollPanel;
    static Font font;
    //static URERenderer renderer;
    static URERenderer renderer;

    static URETerrainCzar terrainCzar;
    static UREThingCzar thingCzar;
    static UREActorCzar actorCzar;

    private void makeWindow() {
        camera = new URECamera(renderer, 1200, 800);
        camera.moveTo(area, 40,20);
        camera.setBounds(0,0,1200,800);

        statusPanel = new UREStatusPanel(renderer, 15, 10, 16, 16, 10, 10, new UColor(1f,1f,1f), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        statusPanel.addText("name", "Player 2",0,0);
        statusPanel.addText("race", "Dorf",0,1);
        statusPanel.addText("class", "Hetero",0,2);
        statusPanel.addText("turn", "T 1", 0, 5);
        statusPanel.addText("time", "", 0, 6);
        statusPanel.setBounds(1200,0,200,800);

        scrollPanel = new UREScrollPanel(renderer, 5, 80, 16, 16, 5, 5, new UColor(1f,1f,1f), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        scrollPanel.addLineFade(new UColor(1.0f, 1.0f, 1.0f));
        scrollPanel.addLineFade(new UColor(0.6f, 0.6f, 0.6f));
        scrollPanel.addLineFade(new UColor(0.4f, 0.4f, 0.4f));
        scrollPanel.addLineFade(new UColor(0.3f, 0.3f, 0.3f));
        scrollPanel.setBounds(0,800,1400,200);
        scrollPanel.print("Welcome to UnRogueEngine!");
        scrollPanel.print("The universal java toolkit for roguelike games.");
        scrollPanel.print("Your journey begins...");

        commander.setStatusPanel(statusPanel);
        commander.setScrollPanel(scrollPanel);
    }

    public void startUp()  {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/Px437_Phoenix_BIOS-2y.ttf")).deriveFont(Font.PLAIN, 16);
        } catch (Exception e) {
            System.out.println("Failed to load font");
        }
        //renderer = new URERenderer(font);
        renderer = new URERendererOGL(font);
        renderer.initialize();

        terrainCzar = new URETerrainCzar();
        terrainCzar.loadTerrains("/terrains.json");
        thingCzar = new UREThingCzar();
        thingCzar.loadThings("/things.json");
        actorCzar = new UREActorCzar();
        actorCzar.loadActors("/actors.json");

        area = new UREArea(100, 100, terrainCzar, "wall");
        //URELandscaper scaper = new ExampleForestScaper(terrainCzar, thingCzar);
        URELandscaper scaper = new ExampleCaveScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);

        player = new UREPlayer("Player", '@', new UColor(Color.WHITE), true, new UColor(0.3f, 0.3f, 0.6f), 3, 4);

        commander = new URECommander(player, renderer, thingCzar, actorCzar);
        renderer.setCommander(commander);
        area.setCommander(commander);
        makeWindow();//.getContentPane().addKeyListener(commander);

        commander.registerScrollPrinter(scrollPanel);
        commander.registerTimeListener(area);
        commander.registerTimeListener(this);
        commander.addAnimator(camera);

        UCell startcell = scaper.randomOpenCell(area, player);
        player.moveToCell(area, startcell.x, startcell.y);
        player.attachCamera(camera, URECamera.PINSTYLE_HARD);
        player.startActing(commander);

        UREActor monk = actorCzar.getActorByName("monk");
        UCell monkdest = scaper.randomOpenCell(area, monk);
        monk.moveToCell(area, monkdest.x, monkdest.y);
        monk.startActing(commander);

        for (int i=0;i<30;i++) {
            UREActor rat = actorCzar.getActorByName("rat");
            UCell ratdest = scaper.randomOpenCell(area, rat);
            rat.moveToCell(area, ratdest.x, ratdest.y);
            rat.startActing(commander);
        }

        commander.gameLoop();
    }

    public void hearTimeTick(URECommander commander) {
        statusPanel.setText("turn", "T " + Integer.toString(commander.getTurn()));
        statusPanel.setText("time", commander.timeString(true, " "));
    }
}
