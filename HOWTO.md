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

