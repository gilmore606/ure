# Getting started with URE

## 1. Clone the repository

Clone the URE github repo into a fresh project folder on your local workstation.

```
git clone https://github.com/gilmore606/ure.git
```

## 2. Import the project

Import the ``build.gradle`` file from the project root into your IDE as a new project.  URE is mostly developed with IntelliJ IDEA;
however, any Java IDE such as Eclipse that supports Gradle integration should work.  Gradle should auto-install and then pull in the
dependencies for the project.

## 3. Create a runconfig

(IntelliJ) Click Edit Configuration... in the upper right next to the green Run button, and create a new Gradle run config.
For the Gradle task enter 'run'.  You should now be able to click Run to build and run URE's example game.

## 4. Start hacking

Although URE is provided as an importable package, and can be used this way for some purposes, most game developers will want to
customize and extend the URE base classes such as Thing and Actor.  This requires operating directly on the URE source.

We recommend you follow some simple practices to ensure minimal merge conflicts in the future, should you choose to merge URE
updates and fixes back into your project.

- Use the event bus to respond to game events, rather than hacking directly into engine methods to trigger on things that happen in the engine.
- If you must add to a URE method, call out to a method of your own which does all the work; minimize the change you make to the URE methods.

A good way to get started is to follow the included ExampleGame as a pattern.  
- Create a new package in the project for your game.  
- Make a new main class for your game in that package, and modify ExampleMain.java to call your main class.  
- Use the code from ExampleGame.startUp() as a starting point.


# A note about @Inject

The first unusual thing you'll notice in ExampleGame is probably this:

```
@Inject
UCommander commander;
```

This declaration is called a **dependency injection**.  It's simply a shortcut to make certain singleton (one instance only) objects available to many classes without having to pass in a reference.  'commander' in particular is injected into almost
every class, for convenience of calling control methods and accessing configuration.  For this injection to work in your classes (your main game class, and any new classes you create) you'll need to declare it as above with the @Inject header.  You'll also need to do this in your constructor to actually acquire the reference:

```
  public ExampleGame() {
        Injector.getAppComponent().inject(this);
```

Finally, you'll need to add a line to ure.sys.dagger.AppComponent.java interface spec to tell the injector about your class:

```
void inject(ExampleGame game);
```

# Key components

Let's briefly look at the main component objects of a URE game, in the order we encounter them in the ExampleGame.startUp() sequence.

## UCommander

A UCommander instance is the central control hub of the game; it runs the main gameLoop(), listens for player input, controls
UI, provides configuration access, and handles other system functions.  Your main class will create a UCommander during startup, and then hand control to it by calling commander.gameLoop(), which runs until the player quits.

## UConfig

The Commander creates a UConfig instance to hold all configuration settings for the engine.  Although you could edit UConfig directly to change settings, we recommend changing them by calling setter methods in your game's startup, to minimize conflicts with future URE updates.

## URendererOGL

The Renderer knows how to render a Camera onto the screen.  The default RendererOGL is an ASCII renderer (using OpenGL to create the glyphs), but other renderers are planned, such as a graphical tile renderer.

## UPlayer

A Player represents the player.  It extends Actor (which extends Thing) and mostly acts like a normal Actor.  You'll probably make just one in your startup, however there's no hard rule that says only one can exist.

## UCartographer and Area

The Cartographer serves Areas (game levels) on demand as the player moves through the gameworld.  An Area represents a single contiguous game map as a 2D grid of Cell objects.  Each Cell has a single Terrain, and can hold Actors and Things.

You can use the default
Cartographer class without extending it, however you will need to supply it with at least one Region.  A Region represents a
'stack' of linearly connected Areas which share some characteristics such as size dimensions, flavor tags, and generator
algorithms.  Once your Cartographer is configured with your initial world structure, you can generate the starting area with
Cartographer.getStartArea() and move the Player into it.

Cartographer will also manage saving areas to disk (by default, when the player is separated from them by one intervening area) and loading them from disk again (by default, when the player is one exit away from a new area).  This happens in the
background and should be transparent to you and the player.


## StatusPanel, ScrollPanel, LensPanel

These panel UI classes relay game information via the Commander, but they aren't required for the engine to operate.

## View

URE roughly follows a Model-View-Controller (MVC) architecture.  The View object represents a rendering order tree to allow
components to draw to the screen.  You will create a root View for your game and addChild() other views to it to give them
access to the renderer.

## Camera

Camera represents a panel which uses the Renderer to show a portion of an Area.  You will create a Camera and add it to your
View, and probably not interact with it much beyond that.  More than one Camera can exist, and they can be pinned to other
actors besides the Player, or pointed at static locations.

## ThingCzar, TerrainCzar, ActorCzar

The -Czar objects are singletons which generate new instances of game entities.  Czar reads from a .json file to create all
possible types of its entity, and serves new copies of those types to the game as needed.  These singletons are often @Inject'ed into other classes for convenience to allow easy spawning of entities into the world.

You don't need to create these objects yourself (as the injector will take care of this) and you shouldn't need to modify them.  They load from default filenames (things.json, terrain.json, actors.json) but have methods to reload from any provided .json file.



