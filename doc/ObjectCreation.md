# Object Creation

New game entities are created at runtime by the Czar objects -- TerrainCzar, ThingCzar, and ActorCzar.

These are singletons which generate new instances of game entities.  Czar reads from a .json file to create all
possible types of its entity, and serves new copies of those types to the game as needed.  These singletons are often @Inject'ed into other classes for convenience to allow easy spawning of entities into the world.  You don't need to create these objects yourself (as the injector will take care of this) and you shouldn't need to modify them.  

The czars create their libraries of entity types by reading JSON config files located in the resources folder.   By default these files are things.json, terrain.json, and actors.json, but you can reload them with any filename.  The format of these files is simple: a JSON list of property pairs corresponding to the properties on those base classes.  Default values assigned in the class definitions can be omitted from the JSON.  Any new properties you add to these classes can also be set through these files.

On startup, the czar reads the JSON and creates a template object for each entry, and stores a hashmap of these templates by name.  When a new object is requested from the czar it simply clones the template and returns the fresh new object.

The 'type' field is special, as this controls the subclass the object will assume.  'type' is checked against the .type field of all subclasses of the base class discovered (for ThingCzar, all subclasses of UThing); the first subclass found whose .type matches the JSON type entry is used.

An example of spawning a new UThing:
```
  UThing item = thingCzar.getThingByName("apple");
```
Notice we get the new thing by its name -- this is the name referenced in the things.json file.  For this reason, these names must be unique.  Now you can move this item into a Cell, Actor, etc. to place it in the gameworld.
