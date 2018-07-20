# Rendering

The basic rendering of your gameworld into a player's viewpoint is done automatically if you've made a [Camera](doc/Cameras.md).
On every frame, the Camera will ask the Renderer to draw terrain, thing, and actor glyphs; various methods on those entities
such as getGlyph(), getBackgroundColor(), etc. control their appearance.

However, you may wish to render other things onto the screen -- for instance when making a Modal UI popup.  In these cases you'll
need to access the Renderer directly.  A Modal's .draw() method is given a reference to the Renderer; in other cases you
will have to get it from Commander.

In your drawing methods you'll call a few low level drawing primitives on Renderer to put images on screen.  These methods all
use pixel coordinates relative to the upper left corner of the view context; usually this maps to 0,0 of your Camera view.  To
position correctly to draw in a particular glyph row/column position, you'll need to ask Renderer for glyphWidth() and glyphHeight()
and multiply by these to find your pixel coordinates.

You should only do this kind of low level drawing in a context called during screen updates, for instance a Modal.draw().  Outside
of such contexts, such as in the code of actions or various event handlers, any drawing you do will likely be overwritten on
the next frame update.  To affect the screen from such methods, set a property somewhere that will be read by a screen-updating
method.

```
Renderer.drawString(int x, int y, UColor color, String text)
```
drawString draws the given text at the given pixel coordinates in the given [color](doc/Colors.md), in the configured textFont.
```
Renderer.drawGlyph(int glyph, int x, int y, UColor color)
```
drawGlyph draws the given glyph at the given pixel coordinates in the given [color](doc/Colors.md), from the configured glyph font.
```
Renderer.drawGlyphOutline(int glyph, int x, int y, UColor color)
```
drawGlyphOutline draws a thickened outline of the given glyph.  This is used in URE to (by default) draw black outlines around
actors.
```
Renderer.drawRect(int x, int y, int w, int h, UColor color)
```
Draw a solid rectangle of color at the given coordinates with the given width and height.
```
Renderer.drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor)
```
Draw a rectangle outline of a given thickness with the given background color (set this color's alpha to 0 for no fill).

