# Cartographer and world structure

The Cartographer serves Areas (game levels) on demand as the player moves through the gameworld. An Area represents a single contiguous game map as a 2D grid of Cell objects. Each Cell has a single Terrain, and can hold Actors and Things.

You can use the default Cartographer class without extending it, but you will need to supply it with at least one [Region](Regions.md). (A Region represents a 'stack' of linearly connected Areas which share some characteristics such as size dimensions, flavor tags, and generator algorithms supplied by [Landscapers](Landscaper.md)).  You'll also need to set
the cartographer.startArea property to the 'label' of the first area you intend to create.

Labels are an important concept in URE world structure.  A label is simply a string uniquely denoting a single game Area.  Labels are of the form:
```
regionname <number>
```
where <number> indicates the level or 'depth' within the [region](Regions.md), each one being its own area.  When Cartographer is asked for
an area for a certain label, it looks at the regionname, then asks the region whose name corresponds to that string for
a level with the given number.  See the [regions document](doc/Regions.md) for more on how this happens.  Every area has
such a unique label, and every [Stairs](Stairs.md) exit entity has a label indicating what area it connects to.

For a predefined world structure, you can simply generate all of your [Regions](Regions.md) on Cartographer startup, but
don't limit yourself to this concept.  In ExampleGame, most of the regions are generated dynamically at runtime -- some
runs might have many caverns, some might have very few.

You can either create your first region and set the cartographer's startArea in your game's main code, or you can create
a custom MyCartographer subclass which does this in its overridden setupRegions() (as ExampleGame does).  Note that if you override setupRegions() you must call the superclass method to make sure disk-saved regions are reloaded on startup.  You
should set your cartographer's start area to an area in this first region (so it can actually get created):
```
cartographer.setStartArea("myregion 1");
```
Once your Cartographer is configured with your initial world structure, you can generate the starting area:
```
UArea area = cartographer.getStartArea();
```
and then move the player into it.  At this point, the system will continue generating or loading new areas on demand as the
player moves through inter-area transitions (known generically as [Stairs](Stairs.md)), checking their .label strings
and asking the cartographer for new areas.  By default, the Cartographer does this proactively -- when the player enters a
new area, Cartographer will load (or generate) all areas directly connected to the new area.  This happens in the background,
so those Areas are ready when the player enters a Stairs and travels to one.  Similarly, old areas that aren't directly
connected to the player's current area will be harvested, persisted to disk, and removed from memory.  This means any area
can be entered immediately at any time, with no load time.

