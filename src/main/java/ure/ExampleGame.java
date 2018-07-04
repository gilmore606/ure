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
        URELandscaper scaper = new URELandscaper(terrainCzar, thingCzar);
        // TODO: make a custom URECaveScaper that just calls all this stuff with some params
        scaper.digCaves(area, "floor",2, 2, 98, 98, 0.38f + scaper.random.nextFloat() * 0.14f,
                4 + scaper.random.nextInt(3), 3 + scaper.random.nextInt(3),
                2 + scaper.random.nextInt(3));
        if (scaper.random.nextFloat() < 0.4f)
            scaper.digRiver(area, "water", 0, 0, 99, 99, 2f + scaper.random.nextFloat() * 4f,
                    0.7f, 1.4f);
        if (scaper.random.nextFloat() < 0.3f)
            scaper.digRiver(area, "water", 0, 0, 99, 99, 3f, 0.9f, 2f);
        if (scaper.random.nextFloat() < 0.3f)
            scaper.digRiver(area, "lava", 0, 0, 99, 99, scaper.random.nextFloat() * 4f + 1f,
                    0.5f + scaper.random.nextFloat()*2f, 1f + scaper.random.nextFloat());
        scaper.addDoors(area, "door", new String[]{"wall"}, 0.1f + scaper.random.nextFloat());
        for (int i=0;i<scaper.random.nextInt(6);i++) {
            int width = scaper.random.nextInt(5)+2;
            int height = scaper.random.nextInt(5)+2;
            int[] boxloc = scaper.locateBox(area, width, height, new String[]{"floor"});
            if (boxloc != null) {
                scaper.drawRect(area, "carvings", boxloc[0], boxloc[1], boxloc[0] + width, boxloc[1] + height);
                if (scaper.random.nextFloat() < 0.5f) {
                    scaper.spawnThingAt(area, boxloc[0] + (width / 2), boxloc[1] + (height / 2), "gold statue");
                    scaper.spawnLightAt(area, boxloc[0] + (width/2), boxloc[1] + (height/2),
                            new UColor(1f, 1f, 0.7f), (width+height)/2, 15);
                }
            }
        }
        scaper.simplexScatterTerrain(area, "floormoss", new String[]{"floor"}, 0.4f + scaper.random.nextFloat() * 0.3f, scaper.random.nextFloat() * 0.6f);
        scaper.simplexScatterThings(area, "skull", new String[]{"floor","floormoss"}, 0.6f, 0.15f + scaper.random.nextFloat() * 0.3f);
        scaper.scatterThings(area, new String[]{"trucker hat", "butcher knife", "rock", "apple"}, new String[]{"floor"}, 10 + scaper.random.nextInt(40));

        player = new UREPlayer("Player", '@', new UColor(Color.WHITE), true, 4, 6);

        commander = new URECommander(player, renderer);
        renderer.setCommander(commander);
        area.setCommander(commander);
        makeWindow();//.getContentPane().addKeyListener(commander);

        commander.registerScrollPrinter(scrollPanel);
        commander.registerTimeListener(area);
        commander.registerTimeListener(this);
        commander.addAnimator(camera);

        UCell startcell = scaper.randomCell(area, new String[]{"floor"});
        player.moveToCell(area, startcell.x, startcell.y);
        player.attachCamera(camera, URECamera.PINSTYLE_HARD);
        player.startActing(commander);

        UREActor monk = actorCzar.getActorByName("monk");
        UCell monkdest = scaper.randomCell(area, new String[]{"floor"});
        monk.moveToCell(area, monkdest.x, monkdest.y);
        monk.startActing(commander);

        commander.gameLoop();
    }

    public void hearTimeTick(URECommander commander) {
        statusPanel.setText("turn", "T " + Integer.toString(commander.getTurn()));
        statusPanel.setText("time", commander.timeString(true, " "));
    }
}
