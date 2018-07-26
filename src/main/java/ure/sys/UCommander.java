package ure.sys;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import ure.actions.ActionWalk;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.commands.UCommand;
import ure.sys.events.PlayerChangedAreaEvent;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.events.TimeTickEvent;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.UCamera;
import ure.ui.modals.*;
import ure.ui.panels.UScrollPanel;
import ure.ui.panels.UStatusPanel;
import ure.ui.USpeaker;
import ure.vaulted.VaultedArea;
import ure.vaulted.VaultedModal;

import javax.inject.Inject;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;


/**
 * UCommander is a singleton class (you only make one of these!) which receives player input, converts it to
 * commands, runs the game loop, tracks NPC/player action time, and tracks world turn/time.  Think of it as the
 * central control hub of a URE game.
 *
 */


public class UCommander implements URenderer.KeyListener,HearModalGetString,HearModalStringPick {

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    EventBus bus;

    public UConfig config;
    public Random random;
    public USpeaker speaker;

    private HashSet<UAnimator> animators;
    private ArrayList<UActor> actors;

    private UREGame game;
    private URenderer renderer;
    private UPlayer player;
    private UScrollPanel scrollPrinter;
    private UCamera modalCamera;


    private UScrollPanel scrollPanel;
    private UStatusPanel statusPanel;

    public UThingCzar thingCzar;
    public UActorCzar actorCzar;
    public UCartographer cartographer;

    public int turnCounter = 0;
    public int frameCounter;

    private boolean breakLatchOnInput = true;

    private HashMap<GLKey, UCommand> keyBindings;
    private LinkedBlockingQueue<GLKey> keyBuffer;
    private int keyBufferSize = 2;

    private boolean waitingForInput = false;
    private boolean moveLatch = false;
    private int moveLatchX = 0;
    private int moveLatchY = 0;

    private UModal modal;
    private Stack<UModal> modalStack;

    private boolean quitGame = false;

    public UCommander() {
        Injector.getAppComponent().inject(this);
        bus.register(this);
        config = new UConfig();
        random = new Random();
    }
    public void registerComponents(UREGame _game, UPlayer theplayer, URenderer theRenderer, UThingCzar thingczar, UActorCzar actorczar, UCartographer carto) {
        game = _game;
        renderer = theRenderer;
        animators = new HashSet<UAnimator>();
        actors = new ArrayList<UActor>();
        thingCzar = thingczar;
        actorCzar = actorczar;
        cartographer = carto;

        setPlayer(theplayer);
        readKeyBinds();
        renderer.setKeyListener(this);
        keyBuffer = new LinkedBlockingQueue<GLKey>();
        speaker = new USpeaker();
        speaker.initialize();
        addAnimator(speaker);
        modalStack = new Stack<>();
        actorCzar.loadActors("/actors.json");
    }

    public long generateNewID(Entity entity) {
        return random.nextLong();
    }

    public int getTurn() { return turnCounter; };

    public void setPlayer(UPlayer theplayer) {
        player = theplayer;
    }

    public boolean isQuitGame() { return quitGame; }

    /**
     * Newly spawned actors must register with the commander to get action time and thereby...act.
     *
     */
    public void registerActor(UActor actor) {
        if (!actors.contains(actor))
            actors.add(actor);
    }
    public void unregisterActor(UActor actor) {
        actors.remove(actor);
        if (actors.contains(actor))
            System.out.println("****IMPOSSIBLE BUG : actor removed from commander.actors but still there");
    }

    /**
     * Register a UI component to print scroll messages.  Right now this can only be a UScrollPrinter.
     *
     */
    public void registerScrollPrinter(UScrollPanel printer) {
        scrollPrinter = printer;
    }

    /**
     * Register the camera to center modals on (if UConfig.modalPosition = POS_CAMERA_CENTER).
     *
     */
    public void registerModalCamera(UCamera camera) { this.modalCamera = camera; }
    public UCamera modalCamera() { return modalCamera; }

    public void addAnimator(UAnimator animator) { animators.add(animator); }
    public void removeAnimator(UAnimator animator) { animators.remove(animator); }

    public UActor player() { return player; }

    /**
     * Read keybinds.txt and map keys to commands.
     *
     * Commands are subclasses of UCommand; use the 'id' string of the particular subclass in the keybinds.txt file.
     *
     */
    public void readKeyBinds() {
        keyBindings = new HashMap<GLKey, UCommand>();
        HashMap<String,Integer> glmap = new HashMap<>();
        Class constantsClass = org.lwjgl.glfw.GLFW.class;
        for (Field field : constantsClass.getDeclaredFields()) {
            if (field.getName().startsWith("GLFW_KEY_")) {
                try {
                    glmap.put(field.getName(), field.getInt(null));
                    System.out.println("REFLECT: read GLFW keymap pair " + field.getName() + " as " + Integer.toString(field.getInt(null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        List<String> lines;
        try (InputStream inputStream = getClass().getResourceAsStream("/keybinds.txt")) {
            lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Can't load keybinds.txt", e);
        }
        Reflections reflections = new Reflections("ure.commands", new SubTypesScanner());
        Set<Class<? extends UCommand>> commandClasses = reflections.getSubTypesOf(UCommand.class);
        HashMap<String,Class> commandMap = new HashMap<>();
        try {
            for (Class<? extends UCommand> commandClass : commandClasses) {
                Field idField = commandClass.getField("id");
                String idValue = (String) idField.get(null);
                if (idValue != null) {
                    System.out.println("REFLECT: found command " + idValue);
                    commandMap.put(idValue, commandClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String line : lines) {
            int comma = line.indexOf(",");
            String commandid = line.substring(comma+1,line.length());
            String keystr = line.substring(0,comma);
            boolean shiftkey = false;
            boolean ctrlkey = false;
            int plus = keystr.indexOf("+");
            if (plus > 0) {
                String modstr = line.substring(0,plus);
                if (modstr.equals("shift") || modstr.equals("SHIFT"))
                    shiftkey = true;
                if (modstr.equals("ctrl") || modstr.equals("CTRL") || modstr.equals("control") || modstr.equals("CONTROL"))
                    ctrlkey = true;
                keystr = keystr.substring(plus+1,keystr.length());
            }
            int k = glmap.get("GLFW_KEY_" + keystr.toUpperCase());
            GLKey glkey = new GLKey(k, shiftkey, ctrlkey);
            Class commandClass = commandMap.get(commandid);
            if (commandClass == null) {
                System.out.println("KEYBIND: ERROR - no command found for '" + commandid + "' -- check mapping file!");
            } else {
                try {
                    UCommand cmd = (UCommand) commandClass.newInstance();
                    keyBindings.put(glkey, cmd);
                    System.out.println("KEYBIND: mapping GLKey " + Integer.toString(k) + " to " + cmd.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void keyPressed(GLKey k) {
        keyBuffer.add(k);
    }

    public int mouseX() { return renderer.getMousePosX(); }
    public int mouseY() { return renderer.getMousePosY(); }

    public void consumeKeyFromBuffer() {
        if (!keyBuffer.isEmpty()) {
            GLKey k = keyBuffer.remove();
            UCommand command = null;
            for (GLKey bindkey : keyBindings.keySet()) {
                if (bindkey.sameKeyAs(k))
                    command = keyBindings.get(bindkey);
            }
            hearCommand(command, k);
            if (modal == null) {
                if (k.k == GLFW_KEY_1) {
                    debug_1();
                } else if (k.k == GLFW_KEY_2) {
                    debug_2();
                } else if (k.k == GLFW_KEY_Q) {
                    debug();
                } else if (k.k == GLFW_KEY_F1) {
                    launchVaulted();
                } else if (k.k == GLFW_KEY_F2) {
                    showModal(new UModalURESplash());
                }
            }
        }
    }

    void hearCommand(UCommand command, GLKey k) {
        if (command != null && player != null) System.out.println("actiontime " + Float.toString(player.actionTime()) + "   cmd: " + command.id);
        if (modal != null) {
            modal.hearCommand(command, k);
        } else if (command != null) {
            command.execute((UPlayer)player);
        }
    }

    public void setMoveLatch(int xdir, int ydir) {
        moveLatch = true;
        moveLatchX = xdir;
        moveLatchY = ydir;
    }

    /**
     * Cancel player latched auto-movement, if engaged.
     * Anything in the world that might be interesting during auto-movement should call this if the player approaches.
     *
     */
    public void latchBreak() {
        moveLatch = false;
        moveLatchX = 0;
        moveLatchY = 0;
    }

    /**
     * Show a UModal dialog and send all player input to it.
     *
     * @param modal
     */
    public void showModal(UModal modal) {
        speaker.playUIsound(config.soundUImodalOpen, 1f);
        attachModal(modal);
        modal.onOpen();
    }

    void debug() {
        player.debug();
    }

    void debug_1() {
        config.setVisibilityEnable(!config.isVisibilityEnable());
    }

    void debug_2() {
        config.setLightEnable(!config.isLightEnable());
    }

    /**
     * Print a message to the scroll printer.
     *
     * @param text
     */
    public void printScroll(String text) {
        scrollPrinter.print(text);
    }

    /**
     * Print a message to the scroll printer if the player can see the source.
     *
     * @param source
     * @param text
     */
    public void printScrollIfSeen(UThing source, String text) {
        if (player != null)
            if (player.canSee(source))
                printScroll(text);
    }

    void animationFrame() {
        if (player != null)
            if (player.area() != null)
                player.area().animationTick();
        for (UAnimator anim : animators) {
            anim.animationTick();
        }
        if (modal != null)
            modal.animationTick();
    }

    public void setStatusPanel(UStatusPanel panel){
        statusPanel = panel;
    }

    public void setScrollPanel(UScrollPanel panel){
        scrollPanel = panel;
    }

    @Subscribe
    public void playerChangedArea(PlayerChangedAreaEvent event) {
        statusPanel.setText("location", cartographer.describeLabel(event.destArea.getLabel()));
    }
    /**
     * The gameLoop() runs forever (until the player exits the game).  It should be the last thing you call from
     * your game's main() after everything is initialized.
     *
     */
    public void gameLoop() {
        long tickRate = 1000000000 / config.getFPStarget();
        long gameTime = System.nanoTime();
        tickTime();
        while (!renderer.windowShouldClose() && !quitGame) {
            frameCounter++;
            renderer.pollEvents();

            //Finalize and flush what we've rendered above to screen.
            renderer.render();

            long curTime = System.nanoTime();
            if (curTime - gameTime > config.getAnimFrameMilliseconds() * 1000)
                animationFrame();
            if (curTime > gameTime + tickRate * 2) gameTime = curTime;
            else gameTime += tickRate;
            while (System.nanoTime() < gameTime) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }

            if (!waitingForInput) {
                tickActors();
                waitingForInput = true;
            }
            // if it's the player's turn, do a command if we have one
            if (waitingForInput) {
                if (player == null) {
                    if (!keyBuffer.isEmpty()) {
                        consumeKeyFromBuffer();
                        tickTime();
                    }
                } else if (!keyBuffer.isEmpty() || moveLatch) {
                    if (moveLatch) {
                        if (breakLatchOnInput && !keyBuffer.isEmpty()) {
                            latchBreak();
                            consumeKeyFromBuffer();
                        } else {
                            player.doAction(new ActionWalk(player, moveLatchX, moveLatchY));
                        }
                    } else {
                        consumeKeyFromBuffer();
                    }
                    renderer.render();
                    if (player != null) {
                        if (player.actionTime() <= 0f) {
                            tickTime();
                        }
                        waitingForInput = false;
                    }
                }
            }
        }
    }

    public void tickActors() {
        // need to use a clone to iterate, since actors might drop out during this loop
        ArrayList<UActor> tmpactors = (ArrayList<UActor>)actors.clone();
        for (UActor actor : tmpactors) {
            if (actors.contains(actor))
                actor.act();
        }
    }
    public void quitGame() {
        quitGame = true;
    }

    public void quitToTitle() {
        persistPlayer();
        player.prepareToVanish();
        player = null;
        cartographer.setupRegions();
        config.setVisibilityEnable(false);
        wipeModals();
        game.setupTitleScreen();
    }

    public void persistPlayer() {
        System.out.println("Persisting player " + player.getName() + "...");
        player.saveStateData();
        String path = savePath();
        File file = new File(path + "player");
        try (
                FileOutputStream stream = new FileOutputStream(file);
                //GZIPOutputStream gzip = new GZIPOutputStream(stream)
        ) {
            JsonFactory jfactory = new JsonFactory();
            JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
            jGenerator.setCodec(objectMapper);
            jGenerator.writeObject(player);
            jGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Post a PlayerChangedAreaEvent after a player level-teleports somehow (anything but a Stairs move).  Normally
     * the Stairs would post this event.
     */
    public void postPlayerLevelportEvent(UArea sourceArea) {
        if (player.area() != sourceArea)
            bus.post(new PlayerChangedAreaEvent(player, null, sourceArea, player.area()));
    }

    public UPlayer loadPlayer() {
        String path = savePath();
        UPlayer p = null;
        try {
            File file = new File(path + "player");
            FileInputStream stream = new FileInputStream(file);
            //GZIPInputStream gzip = new GZIPInputStream(stream)

            p = objectMapper.readValue(stream, UPlayer.class);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        turnCounter = p.saveTurn;
        p.reconnectThings();
        p.setActionTime(0f);
        cartographer.setupRegions();
        return p;
    }

    public void tickTime() {
        for (UActor actor : actors) {
            actor.addActionTime(1f);
        }
        bus.post(new TimeTickEvent(turnCounter));
        turnCounter++;
        System.out.println("time:tick " + Integer.toString(turnCounter));
        renderer.render();
    }

    public int daytimeMinutes() {
        return (int)(((float)((turnCounter + config.getDayTimeStartOffset()) % config.getTurnsPerDay()) / (float)config.getTurnsPerDay()) * 1440f);
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

    void attachModal(UModal newmodal) {
        if (modal != null) {
            modalStack.push(modal);
            modal = null;
        }
        modal = newmodal;
        renderer.getRootView().addChild(modal);
    }

    public void detachModal() {
        renderer.getRootView().removeChild(modal);
        modal = null;
        if (!modalStack.isEmpty())
            modal = modalStack.pop();
    }

    public void detachModal(UModal modal) {
        if (this.modal == modal) {
            detachModal();
        } else {
            if (modalStack.contains(modal))
                modalStack.remove(modal);
        }
    }

    public void wipeModals() {
        while (modal != null)
            detachModal();
    }

    /**
     * Get the filesystem path to the current savestate (the world we're playing now), or the top level save path.
     * @return
     */
    public String savePath() {
        String world = cartographer.worldName();
        if (world == null)
            return config.getSavePath();
        else
            return config.getSavePath() + world + "/";
    }

    void launchVaulted() {
        File dirfile = new File(config.getResourcePath() + "vaults/");
        ArrayList<String> filelist = new ArrayList<>();
        for (String filename : dirfile.list()) {
            if (filename.endsWith(".json")) {
                printScroll("found " + filename);
                filelist.add(filename.substring(0,filename.length()-5));
            }
        }
        filelist.add("<new vaultSet>");

        UModalStringPick spmodal = new UModalStringPick("Select vaultSet to edit:", UColor.COLOR_BLACK, 0, 0,
                filelist, true, this, "vaulted-pickfile");
        printScroll("Launching VaultEd...");
        showModal(spmodal);

    }
    public void hearModalStringPick(String context, String filename) {
        if (filename.equals("<new vaultSet>")) {
            UModalGetString fmodal = new UModalGetString("Filename?", 20, true, UColor.COLOR_BLACK, this, "vaulted-newfile");
            showModal(fmodal);
        } else {
            doLaunchVaulted(filename);
        }
    }
    void doLaunchVaulted(String filename) {
        VaultedArea edarea = new VaultedArea(30,30);
        player.moveToCell(edarea, 2, 2);
        UModal edmodal = new VaultedModal(edarea, filename);
        showModal(edmodal);
    }
    public void hearModalGetString(String context, String input) {
        if (context.equals("vaulted-newfile")) {
            doLaunchVaulted(input);
        }
    }
}
