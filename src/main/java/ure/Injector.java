package ure;

import ure.dagger.AppComponent;
import ure.dagger.AppModule;
import ure.dagger.DaggerAppComponent;

/**
 * This is a static class that provides easy access to our AppModule singleton for dependency
 * injection wherever it's needed.
 */
public class Injector {

    public static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule())
                    .build();
        }
        return appComponent;
    }
}
