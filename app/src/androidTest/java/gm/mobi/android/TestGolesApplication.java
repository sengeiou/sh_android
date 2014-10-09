package gm.mobi.android;

import dagger.ObjectGraph;
import java.lang.reflect.Method;
import org.robolectric.TestLifecycleApplication;

public class TestGolesApplication extends GolesApplication implements TestLifecycleApplication {

    private ObjectGraph objectGraph;

    public void inject(Object o) {
        objectGraph.inject(o);
    }
    @Override public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new GolesTestModule(), new GolesModule(this));
        objectGraph.inject(this);
    }

    @Override public void beforeTest(Method method) {

    }

    @Override public void prepareTest(Object test) {
        //objectGraph.inject(test);
    }

    @Override public void afterTest(Method method) {

    }
}
