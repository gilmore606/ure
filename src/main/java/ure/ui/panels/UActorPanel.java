package ure.ui.panels;

import com.google.common.eventbus.Subscribe;
import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.math.UColor;
import ure.math.UPath;
import ure.render.URenderer;
import ure.sys.events.PlayerChangedAreaEvent;
import ure.sys.events.TimeTickEvent;

import java.util.ArrayList;
import java.util.Collections;

public class UActorPanel extends UPanel {

    ArrayList<UActor> actors;

    public UActorPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
        bus.register(this);
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
        int entryHeight = commander.config.getTileHeight() * 3;
        if (actor.getIcon() == null)
            System.out.println("*** BUG: actor " + actor.getName() + " had null getIcon() at actorpanel");  // this only seems to happen on game reload at first frame when camera is moved into area
        else
            actor.getIcon().draw(renderer, padX, padY + (pos * entryHeight));
        renderer.setFont(URenderer.FontType.TEXT_FONT);
        renderer.drawString(padX + commander.config.getTileWidth() * 2, padY + (pos * entryHeight), fgColor, actor.getName());
        renderer.drawString(padX + commander.config.getTileWidth() * 2, padY + (pos * entryHeight) + commander.config.getTextHeight(), actor.UIstatusColor(), actor.UIstatus());
        renderer.setFont(URenderer.FontType.TILE_FONT);
    }

    @Subscribe
    public void hearPlayerChangedArea(PlayerChangedAreaEvent event) {
        updateActors((UPlayer)commander.player());
    }

    @Subscribe
    public void hearTimeTick(TimeTickEvent event) {
        if (commander.player() != null)
            updateActors((UPlayer)commander.player());
    }
}
