package ure.examplegame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.sys.UConfig;
import ure.sys.UREgame;
import ure.terrain.UTerrainCzar;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.*;
import ure.ui.modals.HearModalTitleScreen;
import ure.ui.modals.UModalTitleScreen;
import ure.ui.panels.UActorPanel;
import ure.ui.panels.ULensPanel;
import ure.ui.panels.UScrollPanel;
import ure.ui.panels.UStatusPanel;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class ExampleGame implements UREgame, HearModalTitleScreen, URenderer.ResolutionListener {

    static UArea area;
    static UCamera camera;
    static UPlayer player;
    static UStatusPanel statusPanel;
    static UScrollPanel scrollPanel;
    static ULensPanel lensPanel;
    static UActorPanel actorPanel;

    @Inject
    URenderer renderer;
    @Inject
    UCommander commander;
    @Inject
    UConfig config;
    @Inject
    UTerrainCzar terrainCzar;
    @Inject
    UThingCzar thingCzar;
    @Inject
    UActorCzar actorCzar;
    @Inject
    UCartographer cartographer;

    private Log log = LogFactory.getLog(ExampleGame.class);

    public ExampleGame() {
        // Set up logging before doing anything else, including dependency injection.  That way we'll
        // get proper logging for @Provides methods.
        try {
            InputStream configInputStream = new FileInputStream(new File("logging.properties"));
            LogManager.getLogManager().readConfiguration(configInputStream);
        } catch (IOException ioe) {
            throw new RuntimeException("Can't configure logger", ioe);
        }

        Injector.getAppComponent().inject(this);
    }

    private void makeWindow() {

        View rootView = new View();
        rootView.setBounds(0, 0, config.getScreenWidth(), config.getScreenHeight());

        camera = new UCamera(0, 0, 1200, 800);
        camera.moveTo(area, 40,20);
        rootView.addChild(camera);

        UColor borderColor = UColor.DARKGRAY;

        statusPanel = new UStatusPanel(200, 200, 10, 10, config.getTextColor(), UColor.BLACK, borderColor);
        statusPanel.addText("name", " ",0,0);
        statusPanel.addText("race", "Owl",0,1);
        statusPanel.addText("class", "Ornithologist",0,2);
        statusPanel.addText("turn", "T 1", 0, 5);
        statusPanel.addText("time", "", 0, 6);
        statusPanel.addText("location", "?", 0, 8);
        statusPanel.addText("lens", "", 0, 20);
        statusPanel.setPosition(1200,0);
        rootView.addChild(statusPanel);

        actorPanel = new UActorPanel(200,600,10,10,config.getTextColor(), UColor.BLACK, borderColor);
        actorPanel.setPosition(1200,200);
        rootView.addChild(actorPanel);

        lensPanel = new ULensPanel(camera, 0, 0, 200, 200, 12, 12, config.getTextColor(), UColor.BLACK, borderColor);
        lensPanel.setPosition(1200,800);
        rootView.addChild(lensPanel);

        scrollPanel = new UScrollPanel(1200, 200, 12, 12, config.getTextColor(), new UColor(0f,0f,0f), new UColor(0.3f,0.3f,0.3f));
        scrollPanel.addLineFade(new UColor(1.0f, 1.0f, 1.0f));
        scrollPanel.addLineFade(new UColor(0.8f, 0.8f, 0.8f));
        scrollPanel.addLineFade(new UColor(0.6f, 0.6f, 0.6f));
        scrollPanel.addLineFade(new UColor(0.5f, 0.5f, 0.5f));
        scrollPanel.addLineFade(new UColor(0.4f, 0.4f, 0.4f));
        scrollPanel.addLineFade(new UColor(0.3f, 0.3f, 0.3f));
        scrollPanel.setPosition(0,800);
        scrollPanel.setBounds(0,800,1200,200);
        scrollPanel.print("Welcome to UnRogueEngine!");
        scrollPanel.print("The universal java toolkit for roguelike games.");
        scrollPanel.print("Your journey begins...");
        rootView.addChild(scrollPanel);

        renderer.setRootView(rootView);
        renderer.setResolutionListener(this);

        commander.setStatusPanel(statusPanel);
        commander.setScrollPanel(scrollPanel);
        commander.registerModalCamera(camera);
    }

    public void startUp()  {

        cartographer = new ExampleCartographer();

        commander.registerComponents(this, player, renderer, thingCzar, actorCzar, cartographer);
        makeWindow();

        commander.registerScrollPrinter(scrollPanel);
        commander.addAnimator(camera);

        setupTitleScreen();

        commander.gameLoop();
    }

    public void setupTitleScreen() {
        scrollPanel.hide();
        lensPanel.hide();
        statusPanel.hide();
        actorPanel.hide();
        area = cartographer.getTitleArea();
        camera.moveTo(area, 50, 50);
        commander.config.setVisibilityEnable(false);
        commander.showModal(new UModalTitleScreen(35, 20, this, "start", area));

    }

    public void hearModalTitleScreen(String context, String optional) {
        if (context.equals("Credits") || context.equals("Quit")) {
            commander.quitGame();
        } else {
            if (context.equals("New World")) {
                cartographer.wipeWorld();
                continueGame(optional);
            } else {
                continueGame(optional);
            }
        }
    }

    public void continueGame(String playername) {
        area.requestCloseOut();
        player = commander.loadPlayer();
        if (player == null) {
            player = makeNewPlayer(playername);
            log.debug("Getting the starting area");
            cartographer.startLoader();
            area = cartographer.makeStartArea();
            UCell startcell = area.randomOpenCell(player);
            player.setSaveLocation(area, startcell.x, startcell.y);
        } else {
             log.info("Loading existing player into " + player.getSaveAreaLabel());
            cartographer.startLoader();
            area = cartographer.getArea(player.getSaveAreaLabel());
        }
        commander.startGame(player, area);
        statusPanel.unHide();
        lensPanel.unHide();
        scrollPanel.unHide();
        actorPanel.unHide();
        player.attachCamera(camera, config.getCameraPinStyle());
    }

    public UPlayer makeNewPlayer(String playername) {
        log.debug("Creating a brand new @Player");
        player = new UPlayer("Player",new UColor(0.1f, 0.1f, 0.4f), 2, 3);
        player.setName(playername);
        player.setID(commander.generateNewID(player));

        UThing item = thingCzar.getThingByName("small stone");
        item.moveTo(player);
        item = thingCzar.getThingByName("trucker hat");
        item.moveTo(player);
        item = thingCzar.getThingByName("apple");
        item.moveTo(player);
        item = thingCzar.getThingByName("nylon backpack");
        item.moveTo(player);
        item = thingCzar.getThingByName("flashlight");
        item.moveTo(player);
        item = thingCzar.getThingByName("biscuit");
        item.moveTo(player);
        item = thingCzar.getThingByName("lantern");
        item.moveTo(player);
        item = thingCzar.getThingByName("butcher knife");
        item.moveTo(player);
        item = thingCzar.getThingByName("hiking boots");
        item.moveTo(player);

        item = thingCzar.getThingByName("army helmet");
        item.moveTo(player);
        item = thingCzar.getThingByName("confusion helmet");
        item.moveTo(player);
        item = thingCzar.getThingByName("aluminum bat");
        item.moveTo(player);
        item = thingCzar.getThingByName("leather jacket");
        item.moveTo(player);
        item = thingCzar.getPile("gold coins", 100);
        item.moveTo(player);
        item = thingCzar.getPile("gold coins", 320);
        item.moveTo(player);
        return player;
    }

    @Override
    public void resolutionChanged(int width, int height) {
        // Position panels around the right/bottom edges
        statusPanel.setPosition(width - statusPanel.getWidth(), 0); // upper right corner
        lensPanel.setPosition(statusPanel.getX(), height - lensPanel.getHeight()); // bottom right
        actorPanel.setBounds(statusPanel.getX(), statusPanel.getHeight() + 1, statusPanel.getWidth(), height - statusPanel.getHeight() - lensPanel.getHeight() - 2);
        scrollPanel.setBounds(0, height - scrollPanel.getHeight(), width - statusPanel.getWidth(), scrollPanel.getHeight());
        camera.setBounds(0, 0, width - statusPanel.getWidth(), height - scrollPanel.getHeight());
        camera.setupGrid();
    }
}
