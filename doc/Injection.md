# Dependency injection

The first unusual thing you'll notice in ExampleGame is probably this:

```
@Inject
UCommander commander;
```

This declaration is called a **dependency injection**.  It's simply a shortcut to make certain singleton (one instance only) objects available to many classes without having to pass in a reference.  'commander' in particular is injected into almost
every class, for convenience of calling control methods and accessing configuration.  URE does this using the Dagger library.

For this injection to work in your classes (your main game class, and any new classes you create) you'll need to declare it as 
above with the @Inject header.  You'll also need to do this in your constructor to actually acquire the reference:
```
  public ExampleGame() {
        Injector.getAppComponent().inject(this);
```
Finally, you'll need to add a line to ure.sys.dagger.AppComponent.java interface spec to tell the injector about your class:
```
void inject(ExampleGame game);
```
If your injected property might be accessed by a subclass outside its package, be sure to declare it public 
(rather than protected or private).

The major URE singletons are already set up to be injected (commander, thingCzar, terrainCzar, actorCzar).  If your game has
other control objects you'd like to inject into classes, you'll also need to call those out in sys.dagger.AppModule, like so:
```
    @Provides
    @Singleton
    public MyController providesMycontroller() {
        MyController con = new MyController();
        MyController.doSomeSetup();
        return con;
    }
```
This creates the singleton to be injected when asked for by other classes.
