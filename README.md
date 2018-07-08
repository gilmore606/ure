# URE

UnRogue Engine is a Java library for making roguelike games.

![ure thumbnail](https://raw.githubusercontent.com/gilmore606/ure/master/thumb1.png)

URE is a class library of smart classes which work together to create a typical
roguelike game environment based on 2D cell grids, turn-based action, and procedural
content.  It can emulate all the conventions of a normal singleplayer roguelike such
as Nethack, but is not limited to these conventions.  Users can use URE simply as a
display layer, as a full world abstraction manager, or take full advtanage of its
object library and use a conventional roguelike as an immediate jumping-off point
to create from.

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


## Using URE

To get started with URE, take a look at the included ExampleGame package.


## Contributing to URE

URE is an open source community project and the maintainers welcome contributions from
the community.  We aim to provide a comprehensive library of useful game objects and
functionality covering as wide a variety of use cases as possible.  If you've made a
generally useful Thing, Behavior, Action, Terrain, or other entity type, ping us with your
branch and we'd be happy to consider merging it back.

URE is built with the SBT build system and depends on LWJGL, Jackson and Reflection.


## API Documentation

see [JavaDoc](https://gilmore606.github.io/ure)

## Projects made with URE

Hopefully someday there will be links here to actual games.
