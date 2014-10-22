package gm.mobi.android;

import android.app.Application;
import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtil;
import dagger.ObjectGraph;
import gm.mobi.android.data.SessionManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.util.LogTreeFactory;
import javax.inject.Inject;
import timber.log.Timber;

public class GolesApplication extends Application {

    private static GolesApplication instance;
    private ObjectGraph objectGraph;

    @Inject SessionManager currentSession;

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
        LogTreeFactory logTreeFactory = objectGraph.get(LogTreeFactory.class);
        for (Timber.Tree tree : logTreeFactory.getTrees()) {
            Timber.plant(tree);
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


    @Deprecated
    public User getCurrentUser() {
        return currentSession.getCurrentUser();
    }

    @Deprecated
    public void setCurrentUser(User currentUser) {
        currentSession.setCurrentUser(currentUser);
    }


    @Deprecated
    public static GolesApplication getInstance() {
        return instance;
    }

    public static GolesApplication get(Context context) {
        return (GolesApplication) context.getApplicationContext();
    }


    public static boolean  isThereInternetConnection(Context context, NetworkUtil networkUtil){
        return networkUtil.isConnected(get(context));
    }

}
