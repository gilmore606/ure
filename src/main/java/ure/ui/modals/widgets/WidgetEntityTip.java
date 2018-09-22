package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.sys.Entity;
import ure.ui.modals.UModal;

import java.util.ArrayList;

public class WidgetEntityTip extends Widget {

    public Entity entity;
    public ArrayList<String> lines;

    public WidgetEntityTip(UModal modal, int x, int y, Entity e) {
        super(modal);
        this.entity = e;
        setClipsToBounds(true);
        lines = new ArrayList<>();
        ArrayList<String> tips = entity.UItips(modal.callbackContext);
        for (String line : tips) {
            ArrayList<String> splitlines = splitLine(line, modal.commander.camera().width / 3);
            for (String splitline : splitlines) {
                lines.add(splitline);
            }
        }
        setDimensions(x,y,modal.longestLine(lines),lines.size()+2);
    }

    ArrayList<String> splitLine(String line, int pixelwidth) {
        ArrayList<String> chunks = new ArrayList<>();
        while (modal.renderer.textWidth(line) > pixelwidth) {
            int spacei = line.indexOf(' ');
            if (spacei < 0) break;
            boolean fits = true;
            int nextspacei = spacei;
            while (fits) {
                spacei = nextspacei;
                nextspacei = line.indexOf(' ', spacei+1);
                if (nextspacei < 0) break;
                fits = (modal.renderer.textWidth(line.substring(0,nextspacei-1)) <= pixelwidth);
            }
            String chunk = line.substring(0,spacei);
            chunks.add(chunk);
            line = line.substring(spacei+1,line.length());
        }
        if (line.length() > 0)
            chunks.add(line);
        return chunks;
    }

    @Override
    public void drawMe() {
        if (entity != null) {
            if (entity.icon() != null) {
                modal.renderer.drawRect(0,0,gw(),gh(),modal.commander.config.getCameraBgColor());
                drawIcon(entity.icon(),0,0);
            }
            drawString(entity.name(), 2, 0);
            int linepos = 2;
            for (String line : lines) {
                modal.renderer.drawString(1, (linepos*gh())+1,modal.commander.config.getCameraBgColor(),line);
                drawString(line, 0, linepos, grayColor());
                linepos++;
            }
        }
    }
}
