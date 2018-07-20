# Landscapers and area generation

A Landscaper is a static (stateless) object with one job: generating new areas on demand.

The default ULandscaper cannot do this!  There are many ways to generate a roguelike area, but no single 'generic' way.
Many people would say the style of area generation defines a roguelike game.  For this reason, we have not included a generic
buildArea() method on ULandscaper.  You will need to create at least one custom ULandscaper class and override a single
method:
```
buildArea(UArea area, int level, String[] tags)
```
'area' is a blank area object passed in already populated with empty Cells, which you will return back in its fully constructed form.

'level' is the level integer from the area label found on the Stairs which brought the player here.

'tags' is a list of Strings passed in from the Region.  Your Landscaper can choose to use these tags to alter the flavor of its activity.


## How do I do that?

The topic of how best to generate a roguelike area map is beyond the scope of this document; if you've gone this far toward making
a game, you hopefully have some ideas on this subject yourself.  For inspiration you can look at the buildArea() methods on the
Landscapers included in the ExampleGame.  These methods call a large number of helper methods on Landscaper to do things like
dig caves, build complexes of rooms, stamp out [vaults](Vaults.md), and place appropriate things and actors.
