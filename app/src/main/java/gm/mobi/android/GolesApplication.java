package gm.mobi.android;

import android.app.Application;
import android.content.Context;
import dagger.ObjectGraph;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.util.FileLogger;
import timber.log.Timber;

public class GolesApplication extends Application {

    private static GolesApplication instance;
    private ObjectGraph objectGraph;
    private User currentUser;

    public GolesApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        plantTrees();
    }

    public void plantTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.plant(new FileLogger.FileLogTree());
        } else {
            //TODO Crashlytics tree
        }

    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    /**
     * Injects the members of {@code instance}, including injectable members
     * inherited from its supertypes.
     *
     * @throws IllegalArgumentException if the runtime type of {@code instance} is
     *     not one of this object graph's {@link dagger.Module#injects injectable types}.
     */
    public void inject(Object o) {
        objectGraph.inject(o);
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    @Deprecated
    public static GolesApplication getInstance() {
        return instance;
    }

    public static GolesApplication get(Context context) {
        return (GolesApplication) context.getApplicationContext();
    }
}
