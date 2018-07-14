package ure.things;

import ure.actions.Interactable;
import ure.math.UColor;
import ure.areas.UArea;
import ure.actors.UActor;
import ure.render.URenderer;
import ure.ui.Icon;

import java.util.Iterator;

public interface UThing  {
    boolean isActor();
    boolean isPlayer();
    void initialize();
    void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline);
    String iname();
    String dname();
    String dnamec();
    String plural();
    char glyph();
    Icon icon();
    String[] UIdetails(String context);
    int glyphOffsetX();
    int glyphOffsetY();
    UColor getGlyphColor();
    boolean drawGlyphOutline();
    void moveToCell(int x, int y);
    void moveToCell(UArea area, int x, int y);
    void moveTo(UContainer container);
    UContainer location();
    void leaveCurrentLocation();
    void addThing(UThing thing);
    void removeThing(UThing thing);
    UCollection contents();
    Iterator<UThing> iterator();
    boolean willAcceptThing(UThing thing);
    int areaX();
    int areaY();
    UArea area();
    boolean tryGetBy(UActor actor);
    void gotBy(UActor actor);
    void droppedBy(UActor actor);
    boolean isInteractable(UActor actor);
    float interactionFrom(UActor actor);
    String getMsg(UActor actor);
    String walkMsg(UActor actor);
    void render(URenderer renderer, int x, int y, UColor light, float vis);
    void emote(String text);
    String name();
}
