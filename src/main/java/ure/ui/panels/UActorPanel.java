package ure.ui.panels;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.math.UColor;
import ure.math.UPath;
import ure.sys.events.PlayerChangedAreaEvent;
import ure.sys.events.TimeTickEvent;

import java.util.ArrayList;
import java.util.Collections;

public class UActorPanel extends UPanel {

    ArrayList<UActor> actors;

    private Log log = LogFactory.getLog(UActorPanel.class);

    public UActorPanel(int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_padx,_pady,_fgColor,_bgColor,_borderColor);
        bus.register(this);
        actors = new ArrayList<>();
    }

    public void updateActors(UPlayer player) {
        actors.clear();
        if (!hidden) {
            for (UActor actor : player.area().getActors()) {
                if ((actor != player) && player.canSee(actor)) {
                    actors.add(actor);
                }
            }
        sortActors(player);
        }
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
    public void drawContent() {
        int i = 0;
        for (UActor actor : actors) {
            drawActor(actor, i);
            i++;
        }
    }

    public void drawActor(UActor actor, int pos) {
        int entryHeight = gh() * 3;
        if (actor.getIcon() == null)
            log.error("*** BUG: actor " + actor.getName() + " had null getIcon() at actorpanel");  // this only seems to happen on game reload at first frame when camera is moved into area
        else
            actor.getIcon().draw(padX, padY + (pos * entryHeight));
        renderer.drawString(padX + gw() * 2, padY + (pos * entryHeight), fgColor, actor.getName());
        renderer.drawString(padX + gw() * 2, padY + (pos * entryHeight) + commander.config.getTextHeight(), actor.UIstatusColor(), actor.UIstatus());
    }

    @Subscribe
    public void hearPlayerChangedArea(PlayerChangedAreaEvent event) {
        if (!hidden)
            updateActors((UPlayer)commander.player());
    }

    @Subscribe
    public void hearTimeTick(TimeTickEvent event) {
        if (commander.player() != null)
            updateActors((UPlayer)commander.player());
    }
}
