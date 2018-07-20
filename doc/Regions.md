# Regions

URegion is a class you instantiate and give to the [Cartographer](Cartographer.md) to tell it about a 'region'.  A region is simply a linearly
connected series of Areas, such as a stacked dungeon of several levels.  The Region defines how big these levels are, how
they are generated, and what other Regions they link to.

When the Cartographer gets a request for a new area, it looks at the label string requested, and splits it into a regionname and
an integer.  It then finds a known Region whose .name matches the regionname, and calls Region.makeArea(integer, label) to
get the Area object.  You can extend Region to provide more complex behavior, but the default URegion can be used to represent basic roguelike
linear dungeons without changes.  

## Making a region

Regions are configured on creation to know how to build their areas:
```
cartographer.addRegion(
   new URegion(
     "paincave",                                     // unique regionname
     "Cave of Pain",                                 // display name
     new ULandscaper[]{new ExampleCaveScaper()},     // array of Landscaper instances
     new String[]{"start","pastoral"},               // array of theme tags
     100, 100,                                       // width and height of areas in this region (in cells)
     15,                                             // number of levels in this region
     "cave entrance",                                // inward-pointing Stairs type
     "cave exit"                                     // outward-pointing Stairs type
));
```
*"paincave"*  The region name must be unique and contain no spaces.\

*"Cave of Pain"*  The display name is used in the UI and need not be unique.  By default it will have a 'depth' description
attached but this can be changed.

*new ULandscaper[]{new ExampleCaveScaper()}*  This list of [Landscaper](Landscaper.md) objects is used to generate areas.  By default, one is
randomly chosen from this pool to generate each area.  You can provide a single Landscaper if all areas should be generated in
the same way; since the Landscaper is given the region's tags and the integer level desired, it can still make the areas
different from level to level (if it has that capability).

*new String[]{"start","pastoral"}*  These theme tags can be used by the [Landscapers](Landscaper.md) to alter the flavor of
generated areas.

*100,100*  All areas generated for a Region are the same size in cells.

*15*  Maximum number of levels to generate.  The first level will contain no default outward exit, and the last will contain no
default inward exit.

*"cave entrance"*  When intra-region [Stairs](Stairs.md) links are made pointing outward (to lower-numbered levels), this named type is created for it.

*"cave exit"* When intra-region [Stairs](Stairs.md) links are made pointing inward (to higher-numbered levels), this named type is created for it.

## Adding links

As mentioned, Region autogenerates Stairs to link between its levels -- the Region itself does this, independent of its
Landscapers, by calling its makeStairs().  By default this method simply finds random open cells to place the requested Stairs types;
override this method to place the Stairs more interestingly.

But what if we have more than one Region?  We must tell our Regions to link to one another, like so:
```
oldregion.addLink(onlevel: 3, exitType: "tunnel", "newregion 1")
newregion.addLink(onlevel: 1, exitType: "tunnel", "oldregion 3")
```
This adds to oldregion level 3 a "tunnel" link leading to newregion 1, and vice versa.  If done on startup, when the player enters
area 'oldregion 3', the oldregion Region will generate that level with a "tunnel" Stairs leading to area 'newregion 1', and the
newregion Region will do the same when it generates area newregion 1.

If you generate Regions on the fly during gameplay, keep in mind that these links are only consulted *before* the relevant Area
objects are actually generated.  Once an Area is generated and persisted, no further links will be made, and calls to addLink to
that region will be useless.  So when you generate a new Region to link to the area the player has just entered, you only
need to addLink() on the new Region, to tell it to make a Stairs back to the current region when it is generated (since you will
be placing the Stairs into the currently generating region yourself).


