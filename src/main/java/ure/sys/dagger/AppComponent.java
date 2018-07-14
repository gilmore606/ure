package ure.sys.dagger;

import dagger.Component;
import ure.render.URendererOGL;
import ure.sys.UCommander;
import ure.actions.UAction;
import ure.actors.UActorCzar;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.behaviors.UBehavior;
import ure.commands.UCommand;
import ure.examplegame.ExampleGame;
import ure.terrain.TerrainI;
import ure.terrain.UTerrainCzar;
import ure.things.ThingI;
import ure.things.UThingCzar;
import ure.ui.UCamera;
import ure.ui.ULensPanel;
import ure.ui.UStatusPanel;
import ure.ui.modals.UModal;

import javax.inject.Singleton;

/**
 * The register for classes that need dependency injection.
 * If you add a class to this register, you'll also need to include
 * Injector.getAppComponent().inject(this);
 * in your constructors to receive the dependencies.  You can then add
 *
 * @Inject
 * UCommander commander
 *
 * (or other injected singletons) to your class and receive the global
 * instance.
 *
 */
@Singleton
@Component(modules =  { AppModule.class })
public interface AppComponent {
    void inject(UTerrainCzar czar);
    void inject(UThingCzar czar);
    void inject(UActorCzar czar);
    void inject(UCartographer cartographer);
    void inject(UCommander cmdr);
    void inject(ExampleGame game);
    void inject(UAction act);
    void inject(ThingI thingi);
    void inject(UArea uarea);
    void inject(UCamera cam);
    void inject(UCell cel);
    void inject(TerrainI terr);
    void inject(UBehavior behav);
    void inject(UModal mod);
    void inject(UCommand comm);
    void inject(URendererOGL rend);
    void inject(UStatusPanel statpan);
    void inject(ULensPanel lenspan);
}
