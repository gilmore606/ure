package ure.ui.panels;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.math.UColor;
import ure.render.URenderer;

import java.util.ArrayList;

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
    }

    @Override
    public void draw(URenderer renderer) {
        super.draw(renderer);
        if (!hidden) {
            int i = 0;
            int entryHeight = commander.config.getGlyphHeight() * 3;
            for (UActor actor : actors) {
                actor.getIcon().draw(renderer, padX, padY + (i * entryHeight));
                renderer.drawString(padX + commander.config.getGlyphWidth() * 2, padY + (i * entryHeight), fgColor, actor.getName());
                renderer.drawString(padX + commander.config.getGlyphWidth() * 2, padY + (i * entryHeight) + commander.config.getTextHeight(), actor.UIstatusColor(), actor.UIstatus());
                i++;
            }
        }
    }
}
