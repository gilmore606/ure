# Configuration

On startup, UCommander creates an instance of UConfig, a class which holds a large number of static configuration properties
that affect various aspects of how URE renders and operates.  You can edit this class directly to change these settings,
but it's recommended that instead you call the setter methods on UConfig during your game's startup, so the class remains 
easily mergeable with future URE updates.

There are many of these properties controlling everything from light blending and UI colors to framerate and area caching.  Most
of them are documented in comments in the UConfig class file.  These settings can be changed at runtime by your game if you wish;
some may produce unexpected behavior if used this way.

These settings aren't persisted in any way; if you change them at runtime, you'll have to change them in that way every time or
you will always have the values defined in UConfig.java.
