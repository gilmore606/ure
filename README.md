# URE - the unRogueEngine

UnRogue Engine is a Java library for making roguelike games.

![ure thumbnail](https://raw.githubusercontent.com/gilmore606/ure/master/thumb1.png)

URE is a class library of smart classes which work together to create a typical
roguelike game environment based on 2D cell grids, turn-based action, and procedural
content.  It can emulate all the conventions of a normal singleplayer roguelike such
as Nethack, but is not limited to these conventions.  Users can use URE simply as a
display layer, as a full world abstraction manager, or take full advtanage of its
object library and use a conventional roguelike as an immediate jumping-off point
to create from.

![ure gif](https://raw.githubusercontent.com/gilmore606/ure/master/thumb2.gif)
## Features

URE is built for flexibility -- you should be able to use as few or as many of its features
as you need.  Its base classes are extendable and configurable to achieve almost any
roguelike game you can imagine.

- Realtime lighting and visibility with full color blending and illumination for
hundreds of lightsources in a scene
- OpenGL rendering layer with no AWT/swing elements, for cross platform high performance
- 60fps animation of lights, terrain, and entities
- Included truetype and pixel CP437-capable monospace fonts, or use any truetype font
- Complete game loop with action-time system and NPC turn management
- Full input handling, key buffering, mappable keybindings
- Persist game areas to disk and restore them seamlessly for a persistent world
- JSON support for defining all types of game entities
- Many level generator algorithms included as generic methods to mix and match for your
own custom level generators
- A* pathfinding available to actor entities or for general problem solving
- Simplex and Perlin noise
- A complete gameworld model incorporating things, actor NPCs, terrain, and more
- Cartographer system for centrally defining a world structure and area links
- UI elements such as a scroll panel, status panels, input widgets, and more
- Built for flexibility -- mix and match URE classes, extend and replace with your own

## Planned features

- Drop-in replacement renderer modules to allow graphical tile rendering, isometric views, or
other visualizations without changing the underlying game
- Complex NPC AI, conversation trees, shops, quest-givers, and other rich NPC features
- Event system with awareness checks
- Status effects
- Object materials and composition
- Environmental effects such as liquid flow, smoke/vapors, and their interactions
- Visual particle system

## Why Java?

The decision to implement URE into Java was not made lightly.  We recognize that there are more popular
languages for roguelike and indie game development, such as Python, Lua, Unity/C#, etc.  However
we feel that Java provides several advantages for roguelike development in particular.

- <B>Robust object model.</B>  The data model of a simulation-style game maps well to a robust inheritance/interface model such as Java's.
Java capabilities such as reflection allow us to dynamically support user-created classes.
- <B>Cross platform.</B> Although a Java app requires a JVM, this can be bundled easily into platform-specific distros to support all major targets.
- <B>Performance.</B> Roguelikes often use CPU-intensive algorithms.  Java implementations can be
significantly faster than their Python or other interpreted-language equivalent.
- <B>Type-safety.</B> More accessible languages like Python tend to be weakly typed.  While this allows
more rapid prototyping and a looser style, it can make maintenance of a large interacting system (such as a
roguelike world state) problematic and brittle.  Java's strong typing helps to keep things on the rails.
- <B>Tools support.</B> Working in Java gives us access to professional strength IDEs such as IntelliJ and their
plugins and build systems.
- <B>A path to Scala.</B> We wanted the option, for ourselves and for users, to incorporate Scala in addition
to vanilla Java code in order to leverage modern functional programming techniques.  This allowed us to mainly
support a well-known language (Java) while still providing a path to a functional language (since Scala is a superset
of Java), rather than writing natively in a newer less-known functional language.
- <B>Future non-Java support.</B> Not only Scala, but Kotlin (or any other JVM-based language) should also be able to import and use URE unmodified.  We have hopes of
building an API frontend for Python and possibly Lua as well.

## Getting Started

To get started with URE, clone the repo and take a look at the included ExampleGame package.  We do not yet provide a jar; coming soon.

If you're excited about URE and would like to start trying it out in its early state, we would welcome your feedback
and be happy to work with you to get features implemented to help you achieve your roguelike dream.


## API Documentation

see [JavaDoc](https://gilmore606.github.io/ure)

## Contributing to URE

URE is an open source community project and the maintainers welcome contributions from
the community.  We aim to provide a comprehensive library of useful game objects and
functionality covering as wide a variety of use cases as possible.  If you've made a
generally useful Thing, Behavior, Action, Terrain, or other entity type, ping us with your
branch and we'd be happy to consider merging it back.



## Projects made with URE

Hopefully someday there will be links here to actual games.

## Built With
- [IntelliJ](https://www.jetbrains.com/idea/) A mighty nice IDE.
- [Gradle](https://gradle.org/) Build automation and dependency management.
- [Jackson](https://github.com/FasterXML/jackson) JSON serializing for object persistence.
- [Reflections](http://static.javadoc.io/org.reflections/reflections/0.9.10/org/reflections/Reflections.html) Dynamic class detection.
- [LWJGL](https://www.lwjgl.org/) Low level OpenGL access.
- [JOML](https://github.com/JOML-CI/JOML) Low level OpenGL math.

