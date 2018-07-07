package ure.things;

import ure.UColor;
import ure.UContainer;
import ure.UREArea;
import ure.actors.UREActor;
import ure.render.URERenderer;

import java.util.Iterator;

public class ThingDeco implements UREThing {

    private UREThing thing;

    public ThingDeco(UREThing realThing) {
        thing = realThing;
    }

    public boolean isActor() { return thing.isActor(); }
    public boolean isPlayer() { return thing.isPlayer(); }
    public void initialize() { thing.initialize(); }
    public void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline) { thing.setDisplayFields(thename, theglyph, thecolor, addOutline); }
    public String iname() { return thing.iname(); }
    public String dname() { return thing.dname(); }
    public String dnamec() { return thing.dnamec(); }
    public String plural() { return thing.plural(); }
    public char glyph() { return thing.glyph(); }
    public int glyphOffsetX() { return thing.glyphOffsetX(); }
    public int glyphOffsetY() { return thing.glyphOffsetY(); }
    public UColor getGlyphColor() { return thing.getGlyphColor(); }
    public boolean drawGlyphOutline() { return thing.drawGlyphOutline(); }
    public void moveToCell(int x, int y) { thing.moveToCell(x, y); }
    public void moveToCell(UREArea area, int x, int y) { thing.moveToCell(area, x ,y); }
    public void moveToContainer(UContainer container) { thing.moveToContainer(container); }
    public void leaveCurrentLocation() { thing.leaveCurrentLocation(); }
    public void addThing(UREThing thething) { thing.addThing(thething); }
    public void removeThing(UREThing thething) { thing.removeThing(thething); }
    public Iterator<UREThing> iterator() { return thing.iterator(); }
    public boolean willAcceptThing(UREThing thething) { return thing.willAcceptThing(thething); }
    public int areaX() { return thing.areaX(); }
    public int areaY() { return thing.areaY(); }
    public UREArea area() { return thing.area(); }
    public boolean tryGetBy(UREActor actor) { return thing.tryGetBy(actor); }
    public void gotBy(UREActor actor) { thing.gotBy(actor); }
    public String getMsg(UREActor actor) { return thing.getMsg(actor); }
    public String walkMsg(UREActor actor) { return thing.walkMsg(actor); }
    public void render(URERenderer renderer, int x, int y, UColor light, float vis) { thing.render(renderer, x, y, light, vis); }
    public void emote(String text) { thing.emote(text); }
    public String name() { return thing.name(); }
}
