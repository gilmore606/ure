package ure.examplegame;

import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.render.URendererOGL;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.sys.UTimeListener;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;
import ure.ui.UCamera;
import ure.ui.UScrollPanel;
import ure.ui.UStatusPanel;
import ure.ui.View;

import javax.inject.Inject;

public class ExampleGame implements UTimeListener {

    static UArea area;
    static UCamera camera;
    static UActor player;
    static UStatusPanel statusPanel;
    static UScrollPanel scrollPanel;
    static URenderer renderer;

    @Inject
    UCommander commander;
    @Inject
    UTerrainCzar terrainCzar;
    @Inject
    UThingCzar thingCzar;
    @Inject
    UActorCzar actorCzar;

    static UCartographer cartographer;

    public ExampleGame() {
        Injector.getAppComponent().inject(this);
    }

    private void makeWindow() {

        View rootView = new View();

        camera = new UCamera(renderer, 0, 0, 1200, 800);
        camera.moveTo(area, 40,20);
        rootView.addChild(camera);

        statusPanel = new UStatusPanel(15, 10, 16, 16, 10, 10, commander.config.getTextColor(), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        statusPanel.addText("name", "Kaffo",0,0);
        statusPanel.addText("race", "Owl",0,1);
        statusPanel.addText("class", "Ornithologist",0,2);
        statusPanel.addText("turn", "T 1", 0, 5);
        statusPanel.addText("time", "", 0, 6);
        statusPanel.addText("location", "Mystic forest", 0, 8);
        statusPanel.setBounds(1200,0,200,800);
        rootView.addChild(statusPanel);

        scrollPanel = new UScrollPanel(5, 80, 16, 16, 5, 5, commander.config.getTextColor(), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
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
        commander.config.setUiFrameGlyphs(null);
    }

    public void startUp()  {
        renderer = new URendererOGL();
        renderer.initialize();

        cartographer = new ExampleCartographer();

        player = new UPlayer("Player", '@', UColor.COLOR_WHITE, true, new UColor(0.3f, 0.3f, 0.6f), 3, 4);

        commander.registerComponents(player, renderer, thingCzar, actorCzar, cartographer);
        cartographer.setCommander(commander);
        area = cartographer.getArea("forest");

        makeWindow();

        commander.registerScrollPrinter(scrollPanel);
        commander.registerTimeListener(this);
        commander.addAnimator(camera);

        UCell startcell = area.randomOpenCell(player);
        player.moveToCell(area, startcell.x, startcell.y);
        player.attachCamera(camera, UCamera.PINSTYLE_HARD);
        player.startActing();

        // commander.speaker.switchBGM("/ultima_wanderer.ogg", 0);

        commander.gameLoop();
    }

    public void hearTimeTick(UCommander cmdr) {
        statusPanel.setText("turn", "T " + Integer.toString(commander.getTurn()));
        statusPanel.setText("time", commander.timeString(true, " "));
    }
}
