package ure.ui.panels;

import ure.math.UColor;
import ure.sys.UAnimator;
import ure.ui.Icons.Icon;

import java.util.ArrayList;

/**
 * ScrollPanel implements the classic scrolling text console.
 *
 * TODO: flash line/panel on activity
 * TODO: track message count since last player turn, pause for anykey if > scrollsize
 */
public class UScrollPanel extends UPanel implements UAnimator {

    int textRows, textColumns;
    int spacing = 1;
    int charWidth, charHeight;
    boolean suppressDuplicates = true;
    String lastMessage;
    ArrayList<String> lines;
    ArrayList<Icon> icons;
    ArrayList<UColor> colors,colorBuffers;
    ArrayList<Float> lineFades;

    float flashLevel;
    float flashDecay = 0.07f;

    public UScrollPanel(int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
        lines = new ArrayList<>();
        icons = new ArrayList<>();
        colors = new ArrayList<>();
        colorBuffers = new ArrayList<>();
        lineFades = new ArrayList<>();
        charWidth = commander.config.getTextWidth();
        charHeight = commander.config.getTextHeight() + spacing;
        textRows = (_pixelh - padY) / charHeight;
        textColumns = (_pixelw - padX) / charWidth;
    }

    public void addLineFade(UColor fade) {
        lineFades.add(fade.grayscale());
    }
    public void addLineFade(float fade) { lineFades.add(fade); }

    public void print(String line) { print(null, line, null); }
    public void print(Icon icon, String line) { print(icon,line,null); }
    public void print(Icon icon, String line, UColor color) {
        if (line != "") {
            if (!(line.equals(lastMessage)) || !suppressDuplicates) {
                lines.add(0, line);
                icons.add(0, icon);
                colors.add(0, color == null ? commander.config.getTextColor() : color);
                colorBuffers.add(0, new UColor(colors.get(0)));
                lastMessage = line;
                flashLevel = 0.7f;
            }
        }
    }

    @Override
    public void draw() {
        if (!hidden) {
            renderer.drawRectBorder(1, 1, width - 2, height - 2, 1, bgColor, borderColor);
            int i = 0;
            boolean fade = !isMouseInside();
            while (i < textRows) {
                if (i < lines.size()) {
                    UColor color = colors.get(i);
                    float gray = 1f;
                    if (fade) {
                        if (i < lineFades.size())
                            gray = lineFades.get(i);
                        else
                            gray = lineFades.get(lineFades.size() - 1);
                    }
                    gray = Math.max(gray, flashLevel);
                    int liney = textRows - (i+1);
                    UColor cbuf = colorBuffers.get(i);
                    cbuf.set(color.fR(), color.fG(), color.fB());
                    cbuf.brightenBy(gray);
                    drawString(lines.get(i), 3, liney, cbuf);
                    drawIcon(icons.get(i), 1, liney);
                }
                i++;
            }
        }
    }

    public void animationTick() {
        if (isMouseInside()) {
            flashLevel = 1f;
            return;
        }
        if (flashLevel > 0f) {
            flashLevel -= flashDecay;
            if (flashLevel < 0f) flashLevel = 0f;
        }
    }
}
