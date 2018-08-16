package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.eventbus.EventBus;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.actors.actions.*;
import ure.math.URandom;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UColor;
import ure.sys.UConfig;
import ure.ui.Icons.Icon;
import ure.ui.Icons.UIconCzar;
import ure.ui.UCamera;
import ure.ui.sounds.USpeaker;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public abstract class UThing implements UContainer, Entity, Interactable, Cloneable {

    public static final String TYPE = "";

    @Inject
    @JsonIgnore
    public UCommander commander;
    @Inject
    @JsonIgnore
    public UConfig config;
    @Inject
    @JsonIgnore
    public UActorCzar actorCzar;
    @Inject
    @JsonIgnore
    public UIconCzar iconCzar;
    @Inject
    @JsonIgnore
    protected URandom random;
    @Inject
    @JsonIgnore
    protected EventBus bus;
    @Inject
    @JsonIgnore
    protected USpeaker speaker;

    protected String name;
    protected long ID;
    protected String iname;
    protected String dname;
    protected String plural;
    protected String type;
    protected String description = "A thing.";
    protected int weight;
    protected boolean movable = true;
    protected int value;
    public String[] equipSlots;
    protected int equipSlotCount = 1;
    public boolean equipped;
    protected String getFailMsg = "You can't pick that up.";
    protected String category = "misc";
    protected HashMap<String,Integer> stats;
    protected String[] tags;
    protected int[] spawnlevels;
    protected String[] spawnterrain;

    protected Icon icon;

    @JsonIgnore
    protected UContainer location;  // What container am I in?

    protected UCollection contents; // What's inside me?

    @JsonIgnore
    protected boolean closed;

    public UThing() {
        Injector.getAppComponent().inject(this);
    }

    /**
     * Set up a new template object fresh from resource JSON deserializing, to make it cloneable.
     */
    public void initializeAsTemplate() {
        setContents(new UCollection(this, this.name));
        stats = new HashMap<>();
        contents = new UCollection();
        equipped = false;
        icon = null;
        icon();
    }

    /**
     * Set up a fresh clone from a template object.
     */
    public void initializeAsCloneFrom(UThing template) {
        stats = (HashMap)template.stats.clone();
        contents = template.contents.clone();
        contents.reconnect(null, this);
        location = null;
        equipped = false;
        icon = null;
        icon();
    }

    public long getID() { return ID; }
    public void setID(long newID) { ID = newID; }

    /**
     * This method is purely for making a unique string ID for debug logging.
     */
    public String NN() { return this.name + " (" + Long.toString(ID) + ")"; }

    public void reconnect(UArea area, UContainer container) {
        this.location = container;
        contents.reconnect(area, this);
        icon().setEntity(this);
    }

    public Icon icon() {
        if (icon == null) {
            icon = iconCzar.getIconByName(name);
            if (icon != null)
                icon.setEntity(this);
        }
        return icon;
    }

    public void closeOut() {
        icon = null;
        stats = null;
        contents.closeOut();
        contents = null;
        closed = true;
        equipped = false;
    }

    public String name() { return name; }
    public int value() { return value + contents.value(); }
    public int weight() { return weight + contents.weight(); }

    public ArrayList<String> UIdetails(String context) {
        ArrayList<String> d = new ArrayList<>();
        d.add("Weight " + Integer.toString(weight()));
        d.add("Value " + Integer.toString(value()));
        return d;
    }

    public void moveToCell(int x, int y) {
        moveToCell(area(), x, y);
    }
    public void moveToCell(UArea area, int x, int y) {
        UCell destination = area.cellAt(x,y);
        if (destination != null) {
            moveTo(destination);
            area.addedThing((UThing) this, x, y);
        }
    }

    public void moveTo(UContainer container) {
        leaveCurrentLocation();
        container.addThing(this);
        this.setLocation(container);
        this.contents.notifyMove();
    }

    public void leaveCurrentLocation() {
        if (getLocation() != null) {
            getLocation().removeThing(this);
            equipped = false;
        }
        this.setLocation(null);
    }

    public void addThing(UThing thing) {
        getContents().add(thing);
    }
    public void removeThing(UThing thing) {
        getContents().remove(thing);
    }
    public Iterator<UThing> iterator() {
        return getContents().iterator();
    }
    public ArrayList<UThing> things() { return getContents().getThings(); }

    public int containerType() { return UContainer.TYPE_THING; }
    public boolean willAcceptThing(UThing thing) {
        return false;
    }

    public int areaX() { return getLocation().areaX(); }
    public int areaY() { return getLocation().areaY(); }

    public int cameraX(UCamera camera) {
        return getLocation().areaX() - camera.leftEdge;
    }
    public int cameraY(UCamera camera) {
        return getLocation().areaY() - camera.topEdge;
    }
    public UArea area() {
        if (getLocation() != null)
            return getLocation().area();
        return null;
    }

    public UThing makeClone() {
        try {
            UThing clone = (UThing) super.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    public boolean tryGetBy(UActor actor) {
        if (!isMovableBy(actor)) {
            if (actor instanceof UPlayer)
                commander.printScroll(getGetFailMsg());
            return false;
        }
        return true;
    }

    public boolean tryEquip(UActor actor) {
        if (!equipped) {
            equipped = true;
            return true;
        }
        return false;
    }

    public boolean tryUnequip(UActor actor) {
        if (equipped) {
            equipped = false;
            return true;
        }
        return false;
    }

    public boolean tryDrop(UContainer dest) {
        moveTo(dest);
        return location == dest;
    }

    public boolean fitsOnBodypart(String part) {
        if (equipSlots == null) return false;
        for (int i=0;i<equipSlots.length;i++) {
            if (equipSlots[i].equals(part))
                return true;
        }
        return false;
    }

    public void gotBy(UActor actor) {
        if (getMsg(actor) != null)
            commander.printScroll(this.getMsg(actor));
    }
    public void droppedBy(UActor actor) {

    }

    public boolean isInteractable(UActor actor) {
        return false;
    }
    public float interactionFrom(UActor actor) {
        return 0f;
    }

    public String getMsg(UActor actor) {
        return getDescription();
    }
    public String walkMsg(UActor actor) { return "You see " + getIname() + "."; }
    public String getCategory() { return category; }
    public void setCategory(String _category) { category = _category; }

    public void emote(String text) { emote(text, null); }
    public void emote(String text, UColor color) {
        commander.printScrollIfSeen(this, text, color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIname() {
        if (iname != null && !iname.isEmpty())
            return iname;
        return "a " + getName();
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getDname() {
        if (dname != null && !dname.isEmpty())
            return dname;
        return "the " + getName();
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getPlural() {
        if (plural != null && !plural.isEmpty())
            return plural;
        char last = getName().charAt(getName().length()-1);
        if (last == 's')
            return getName() + "es";
        return getName() + "s";
    }

    /**
     * Override this to add information about my status.
     */
    public String description() {
        return description;
    }
    public boolean isMovableBy(UActor actor) {
        return isMovable();
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String[] getEquipSlots() { return equipSlots; }
    public void setEquipSlots(String[] s) { equipSlots = s; }
    public int getEquipSlotCount() { return equipSlotCount; }
    public void setEquipSlotCount(int i) { equipSlotCount = i; }
    public boolean isEquipped() { return equipped; }
    public void setEquipped(boolean b) { equipped = b; }
    public String getGetFailMsg() {
        return getFailMsg;
    }
    public void setGetFailMsg(String getFailMsg) {
        this.getFailMsg = getFailMsg;
    }
    public Icon getIcon() {
        return icon;
    }
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    public UContainer getLocation() {
        return location;
    }
    public void setLocation(UContainer location) {
        this.location = location;
    }
    public UCollection getContents() {
        return contents;
    }
    public void setContents(UCollection contents) {
        this.contents = contents;
    }

    public int getStat(String stat) {
        if (stats.containsKey(stat))
            return (int)(stats.get(stat));
        return 0;
    }
    public void setStat(String stat, int value) {
        stats.put(stat, value);
    }


    public boolean isTagAndLevel(String tag, int level) {
        int[] levels = getSpawnlevels();
        if (levels == null || levels.length < 2) return false;
        if (level >= levels[0] && level <= levels[1]) {
            for (String test : getTags()) {
                if (test.equals(tag)) return true;
            }
        }
        return false;
    }

    public boolean canSpawnOnTerrain(String terrain) {
        if (getSpawnterrain() == null) {
            return true;
        }
        for (String t : getSpawnterrain()) {
            if (t.equals(terrain))
                return true;
        }
        return false;
    }

    public boolean isUsable(UActor actor) {
        return false;
    }
    public String useVerb() { return ""; }

    public float useFrom(UActor actor) {
        return 0f;
    }

    public void notifyMove() {

    }


    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int[] getSpawnlevels() {
        return spawnlevels;
    }

    public void setSpawnlevels(int[] spawnlevels) {
        this.spawnlevels = spawnlevels;
    }

    public String[] getSpawnterrain() {
        return spawnterrain;
    }

    public void setSpawnterrain(String[] spawnterrain) {
        this.spawnterrain = spawnterrain;
    }

    public void animationTick() { }

    public HashMap<String, UAction> contextActions(UActor actor) {
        HashMap<String,UAction> actions = new HashMap<>();

        if (isMovableBy(actor))
            actions.put("drop", new ActionDrop(actor, this));
        if (isUsable(actor))
            actions.put(useVerb(), new ActionUse(actor, this));
        if (equipSlots != null) {
            if (equipSlots[0].equals("equip")) {
                if (equipped)
                    actions.put("unequip", new ActionUnequip(actor, this));
                else
                    actions.put("equip", new ActionEquip(actor, this));
            } else {
                if (equipped) {
                    actions.put("remove from " + equipSlots[0], new ActionUnequip(actor, this));
                } else {
                    actions.put("wear on " + equipSlots[0], new ActionEquip(actor, this));
                }
            }
        }
        return actions;
    }

    /**
     * Throw myself away into the void.
     */
    public void junk() {
        leaveCurrentLocation();
    }
}
