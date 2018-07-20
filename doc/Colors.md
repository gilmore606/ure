# Colors and UColor

Colors in URE are almost always referred to using a UColor object.  UColor holds 4 float values for R, G, B and alpha, and is
constructed as such:
```
UColor mycolor = new UColor(0.8f, 0.2f, 0f);
```
but you can also use integers from 0-255 in most places:
```
UColor mycolor = new UColor(180,40,0);
```
You can also include a fourth value, alpha, to specify a transparency:
```
UColor myTransColor = new UColor(0.8f,0.2f,0f,0.5f);
```

UColor is mutable, meaning it can be changed after creation.  This makes it efficient to use for complex chains of color calculation
in rendering and other high-frequency calculations.  However, this can be dangerous.  If you hold a UColor in a property,
be careful what you hand it to lest it be modified further down the chain; if you're passing a UColor into a context where
this might happen (it should be so noted in the API docs) be sure to pass in a new copy of your color, like so:
```
colorEater.eatColor(new UColor(mySavedColor));
```

UColor includes some useful methods for doing color blending operations:
```
float UColor.grayscale()
```
This returns the 0.0-1.0 grayscale value of the color, as calculated by FCC luminance standards.
```
UColor.brightenBy(float intensity)
```
Brighten this color.  Intensity of 1.0f is no change, less is darker, more is brighter.
```
UColor.desaturateBy(float amount)
```
Reduce this color's saturation toward gray.  Below 1.0f reduces saturation, above increases it.  0f will produce a pure grayscale.
```

The UColor class also includes static objects representing common named colors including COLOR_BLACK and COLOR_WHITE, useful
for referring to these common color values in code.  Check the JavaDoc for a full list.
