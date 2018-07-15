package ure.things;

import ure.actions.Interactable;
import ure.math.UColor;
import ure.areas.UArea;
import ure.actors.UActor;
import ure.render.URenderer;
import ure.ui.Icon;

import java.util.Iterator;

public class ThingDeco implements UThing, Interactable {

    private UThing thing;

    public ThingDeco(UThing realThing) {
        thing = realThing;
    }

    public void initialize() { thing.initialize(); }
    public void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline) { thing.setDisplayFields(thename, theglyph, thecolor, addOutline); }
    public String getName() { return thing.getName(); }
    public String getPlural() { return thing.getPlural(); }
    public String getIname() { return thing.getIname(); }
    public String getDname() { return thing.getDname(); }
    public String getDnamec() { return thing.getDnamec(); }
    public String getMsg(UActor actor) { return thing.getMsg(actor); }
    public String walkMsg(UActor actor) { return thing.walkMsg(actor); }
    public String getCategory() { return thing.getCategory(); }
    public char getGlyph() { return thing.getGlyph(); }
    public Icon getIcon() { return thing.getIcon(); }
    public String[] UIdetails(String context) { return thing.UIdetails(context); }
    public int glyphOffsetX() { return thing.glyphOffsetX(); }
    public int glyphOffsetY() { return thing.glyphOffsetY(); }
    public UColor getGlyphColor() { return thing.getGlyphColor(); }
    public boolean drawGlyphOutline() { return thing.drawGlyphOutline(); }
    public void moveToCell(int x, int y) { thing.moveToCell(x, y); }
    public void moveToCell(UArea area, int x, int y) { thing.moveToCell(area, x ,y); }
    public void moveTo(UContainer container) { thing.moveTo(container); }
    public void leaveCurrentLocation() { thing.leaveCurrentLocation(); }
    public UContainer getLocation() { return thing.getLocation(); }
    public void addThing(UThing thething) { thing.addThing(thething); }
    public void removeThing(UThing thething) { thing.removeThing(thething); }
    public Iterator<UThing> iterator() { return thing.iterator(); }
    public UCollection getContents() { return thing.getContents(); }
    public boolean willAcceptThing(UThing thething) { return thing.willAcceptThing(thething); }
    public int areaX() { return thing.areaX(); }
    public int areaY() { return thing.areaY(); }
    public UArea area() { return thing.area(); }
    public boolean tryGetBy(UActor actor) { return thing.tryGetBy(actor); }
    public void gotBy(UActor actor) { thing.gotBy(actor); }
    public void droppedBy(UActor actor) { thing.droppedBy(actor); }
    public boolean isInteractable(UActor actor) { return thing.isInteractable(actor); }
    public float interactionFrom(UActor actor) { return thing.interactionFrom(actor); }

    public void render(URenderer renderer, int x, int y, UColor light, float vis) { thing.render(renderer, x, y, light, vis); }
    public void emote(String text) { thing.emote(text); }
    public int getStat(String stat) { return thing.getStat(stat); }
    public void setStat(String stat, int value) { thing.setStat(stat, value); }
    public boolean isTagAndLevel(String tag, int level) { return thing.isTagAndLevel(tag, level); }
    public boolean canSpawnOnTerrain(String terrain) { return thing.canSpawnOnTerrain(terrain); }
}

