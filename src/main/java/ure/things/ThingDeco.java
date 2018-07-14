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

    public boolean isActor() { return thing.isActor(); }
    public boolean isPlayer() { return thing.isPlayer(); }
    public void initialize() { thing.initialize(); }
    public void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline) { thing.setDisplayFields(thename, theglyph, thecolor, addOutline); }
    public String name() { return thing.name(); }
    public String iname() { return thing.iname(); }
    public String dname() { return thing.dname(); }
    public String dnamec() { return thing.dnamec(); }
    public String plural() { return thing.plural(); }
    public String getMsg(UActor actor) { return thing.getMsg(actor); }
    public String walkMsg(UActor actor) { return thing.walkMsg(actor); }
    public char glyph() { return thing.glyph(); }
    public Icon icon() { return thing.icon(); }
    public String[] UIdetails() { return thing.UIdetails(); }
    public int glyphOffsetX() { return thing.glyphOffsetX(); }
    public int glyphOffsetY() { return thing.glyphOffsetY(); }
    public UColor getGlyphColor() { return thing.getGlyphColor(); }
    public boolean drawGlyphOutline() { return thing.drawGlyphOutline(); }
    public void moveToCell(int x, int y) { thing.moveToCell(x, y); }
    public void moveToCell(UArea area, int x, int y) { thing.moveToCell(area, x ,y); }
    public void moveTo(UContainer container) { thing.moveTo(container); }
    public void leaveCurrentLocation() { thing.leaveCurrentLocation(); }
    public UContainer location() { return thing.location(); }
    public void addThing(UThing thething) { thing.addThing(thething); }
    public void removeThing(UThing thething) { thing.removeThing(thething); }
    public Iterator<UThing> iterator() { return thing.iterator(); }
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
}

