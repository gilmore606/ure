package ure.ui.panels;

import ure.math.UColor;
import ure.render.URenderer;

import java.util.ArrayList;

/**
 * ScrollPanel implements the classic scrolling text console.
 *
 * TODO: flash line/panel on activity
 * TODO: track message count since last player turn, pause for anykey if > scrollsize
 */
public class UScrollPanel extends UPanel {

    int textRows, textColumns;
    int spacing = 1;
    int charWidth, charHeight;
    boolean suppressDuplicates = true;
    String lastMessage;
    ArrayList<String> lines;
    ArrayList<UColor> lineFades;

    public UScrollPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
        lines = new ArrayList<String>();
        lineFades = new ArrayList<UColor>();
        charWidth = commander.config.getTextWidth();
        charHeight = commander.config.getTextHeight() + spacing;
        textRows = (_pixelh - padY) / charHeight;
        textColumns = (_pixelw - padX) / charWidth;
    }

    public void addLineFade(UColor fade) {
        lineFades.add(fade);
    }

    public void print(String line) {
        if (line != "") {
            if (line != lastMessage || !suppressDuplicates) {
                lines.add(0, line);
                lastMessage = line;
            }
        }
    }

    @Override
    public void draw(URenderer renderer) {
        if (!hidden) {
            renderer.drawRectBorder(1, 1, width - 2, height - 2, 1, bgColor, borderColor);
            int i = 0;
            while (i < textRows) {
                if (i < lines.size()) {
                    UColor col;
                    //MM FINISH THIS
                    if (i < lineFades.size())
                        col = lineFades.get(i);
                    else
                        col = lineFades.get(lineFades.size() - 1);
                    renderer.drawString(padX, (padY + pixelh) - ((i + 2) * charHeight), col, lines.get(i));
                }
                i++;
            }
        }
    }
}
