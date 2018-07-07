package ure.things;

import ure.UColor;
import ure.UContainer;
import ure.UREArea;
import ure.actors.UREActor;
import ure.render.URERenderer;

import java.util.Iterator;

public interface UREThing {
    boolean isActor();
    boolean isPlayer();
    void initialize();
    void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline);
    String iname();
    String dname();
    String dnamec();
    String plural();
    char glyph();
    int glyphOffsetX();
    int glyphOffsetY();
    UColor getGlyphColor();
    boolean drawGlyphOutline();
    void moveToCell(int x, int y);
    void moveToCell(UREArea area, int x, int y);
    void moveToContainer(UContainer container);
    void leaveCurrentLocation();
    void addThing(UREThing thing);
    void removeThing(UREThing thing);
    Iterator<UREThing> iterator();
    boolean willAcceptThing(UREThing thing);
    int areaX();
    int areaY();
    UREArea area();
    boolean tryGetBy(UREActor actor);
    void gotBy(UREActor actor);
    String getMsg(UREActor actor);
    String walkMsg(UREActor actor);
    void render(URERenderer renderer, int x, int y, UColor light, float vis);
    void emote(String text);
    String name();
}
