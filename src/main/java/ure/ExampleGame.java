package ure;

import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.render.URenderer;
import ure.render.URendererOGL;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;
import ure.ui.UCamera;
import ure.ui.UScrollPanel;
import ure.ui.UREStatusPanel;
import ure.ui.View;

public class ExampleGame implements UTimeListener {

    static UArea area;
    static UCamera camera;
    static UCommander commander;
    static UActor player;
    static UREStatusPanel statusPanel;
    static UScrollPanel scrollPanel;
    static URenderer renderer;

    static UTerrainCzar terrainCzar;
    static UThingCzar thingCzar;
    static UActorCzar actorCzar;
    static UCartographer cartographer;

    private void makeWindow() {

        View rootView = new View();

        camera = new UCamera(renderer, 0, 0, 1200, 800);
        camera.moveTo(area, 40,20);
        rootView.addChild(camera);

        statusPanel = new UREStatusPanel(15, 10, 16, 16, 10, 10, new UColor(1f,1f,1f), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        statusPanel.addText("name", "Player 2",0,0);
        statusPanel.addText("race", "Dorf",0,1);
        statusPanel.addText("class", "Hetero",0,2);
        statusPanel.addText("turn", "T 1", 0, 5);
        statusPanel.addText("time", "", 0, 6);
        statusPanel.setBounds(1200,0,200,800);
        rootView.addChild(statusPanel);

        scrollPanel = new UScrollPanel(5, 80, 16, 16, 5, 5, new UColor(1f,1f,1f), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        scrollPanel.addLineFade(new UColor(1.0f, 1.0f, 1.0f));
        scrollPanel.addLineFade(new UColor(0.6f, 0.6f, 0.6f));
        scrollPanel.addLineFade(new UColor(0.4f, 0.4f, 0.4f));
        scrollPanel.addLineFade(new UColor(0.3f, 0.3f, 0.3f));
        scrollPanel.setBounds(0,800,1400,200);
        scrollPanel.print("Welcome to UnRogueEngine!");
        scrollPanel.print("The universal java toolkit for roguelike games.");
        scrollPanel.print("Your journey begins...");
        rootView.addChild(scrollPanel);
        renderer.setRootView(rootView);

        commander.setStatusPanel(statusPanel);
        commander.setScrollPanel(scrollPanel);
    }

    public void startUp()  {
        renderer = new URendererOGL();
        renderer.initialize();

        terrainCzar = new UTerrainCzar(null);
        terrainCzar.loadTerrains("/terrains.json");
        thingCzar = new UThingCzar();
        thingCzar.loadThings("/things.json");
        actorCzar = new UActorCzar();
        actorCzar.loadActors("/actors.json");

        cartographer = new UCartographer(terrainCzar, thingCzar);
        area = cartographer.getArea("start");

        player = new UPlayer("Player", '@', UColor.COLOR_WHITE, true, new UColor(0.3f, 0.3f, 0.6f), 3, 4);

        commander = new UCommander(player, renderer, thingCzar, actorCzar, cartographer);
        renderer.setKeyListener(commander);
        area.setCommander(commander);
        makeWindow();//.getContentPane().addKeyListener(commander);

        commander.registerScrollPrinter(scrollPanel);
        commander.registerTimeListener(area);
        commander.registerTimeListener(this);
        commander.addAnimator(camera);

        UCell startcell = area.randomOpenCell(player);
        player.moveToCell(area, startcell.x, startcell.y);
        player.attachCamera(camera, UCamera.PINSTYLE_HARD);
        player.startActing(commander);





        commander.gameLoop();
    }

    public void hearTimeTick(UCommander commander) {
        statusPanel.setText("turn", "T " + Integer.toString(commander.getTurn()));
        statusPanel.setText("time", commander.timeString(true, " "));
    }
}
