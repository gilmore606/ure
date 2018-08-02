package ure.ui.panels;

import com.google.common.eventbus.Subscribe;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.events.TimeTickEvent;

import java.util.HashMap;

public class UStatusPanel extends UPanel {

    int textRows,textColumns;
    int charWidth, charHeight;
    HashMap<String,TextFrag> texts;

    public UStatusPanel(int pixelw, int pixelh, int padx, int pady, UColor fg, UColor bg, UColor borderc) {
        super(pixelw,pixelh,padx,pady,fg,bg,borderc);
        bus.register(this);
        texts = new HashMap<String,TextFrag>();
        textRows = (pixelw-padx) / commander.config.getTextWidth();
        textColumns = (pixelh-pady) / commander.config.getTextHeight();
        charWidth = commander.config.getTextWidth();
        charHeight = commander.config.getTextHeight();
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, fgColor));
    }
    public void addText(String name, String text, int row, int col, UColor color) {
        addTextFrag(new TextFrag(name, text, row, col, color));
    }
    void addTextFrag(TextFrag frag) {
        texts.put(frag.name, frag);
    }

    public void setText(String name, String text) {
        TextFrag frag = texts.get(name);
        frag.text = text;
    }

    @Override
    public void draw(URenderer renderer) {
        super.draw(renderer);
        if (!hidden) {
            for (String textName : texts.keySet()) {
                TextFrag frag = texts.get(textName);
                renderer.drawString(frag.row * charWidth + padX, (frag.col + 1) * charHeight + padY, frag.color, frag.text);
            }
        }
    }

    @Subscribe
    public void hearTimeTick(TimeTickEvent event) {
        if (commander.player() != null) {
            setText("turn", "turn " + Integer.toString(event.turn));
            setText("time", commander.timeString(true, " "));
            setText("location", commander.cartographer.describeLabel(commander.player().area().getLabel()));
            setText("name", commander.player().getName());
        }
    }

}
