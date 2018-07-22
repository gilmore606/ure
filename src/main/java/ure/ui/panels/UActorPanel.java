package ure.ui.panels;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.math.UColor;
import ure.math.UPath;
import ure.render.URenderer;

import java.util.ArrayList;
import java.util.Collections;

public class UActorPanel extends UPanel {

    ArrayList<UActor> actors;

    public UActorPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
        actors = new ArrayList<>();
    }

    public void updateActors(UPlayer player) {
        actors.clear();
        for (UActor actor : player.area().getActors()) {
            if ((actor != player) && player.canSee(actor)) {
                actors.add(actor);
            }
        }
        sortActors(player);
    }

    public void sortActors(UPlayer player) {
        Collections.sort(actors, (a,b) -> isAcloser(a,b,player));
    }

    public int isAcloser(UActor a, UActor b, UPlayer player) {
        int dista = UPath.mdist(a.areaX(), a.areaY(), player.areaX(), player.areaY());
        int distb = UPath.mdist(b.areaX(), b.areaY(), player.areaX(), player.areaY());
        if (dista < distb)
            return -1;
        else if (distb < dista)
            return 1;
        else
            return 0;
    }

    @Override
    public void draw(URenderer renderer) {
        super.draw(renderer);
        if (!hidden) {
            int i = 0;
            for (UActor actor : actors) {
                drawActor(renderer, actor, i);
                i++;
            }
        }
    }

    public void drawActor(URenderer renderer, UActor actor, int pos) {
        int entryHeight = commander.config.getGlyphHeight() * 3;
        actor.getIcon().draw(renderer, padX, padY + (pos * entryHeight));
        renderer.drawString(padX + commander.config.getGlyphWidth() * 2, padY + (pos * entryHeight), fgColor, actor.getName());
        renderer.drawString(padX + commander.config.getGlyphWidth() * 2, padY + (pos * entryHeight) + commander.config.getTextHeight(), actor.UIstatusColor(), actor.UIstatus());
    }
}
