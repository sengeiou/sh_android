package gm.mobi.android;

import dagger.ObjectGraph;
import java.lang.reflect.Method;
import org.robolectric.TestLifecycleApplication;
import timber.log.Timber;

public class TestGolesApplication extends GolesApplication implements TestLifecycleApplication {

    private ObjectGraph objectGraph;

    public void inject(Object o) {
        objectGraph.inject(o);
    }
    @Override public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new GolesTestModule(), new GolesModule(this));
        objectGraph.inject(this);
    }

    @Override
    public void plantTrees() {
        Timber.plant(new Timber.Tree() {

            String formatString(String message, Object... args) {
                // If no varargs are supplied, treat it as a request to log the string without formatting.
                return args.length == 0 ? message : String.format(message, args);
            }
            @Override public void v(String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void v(Throwable t, String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void d(String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void d(Throwable t, String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void i(String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void i(Throwable t, String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void w(String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void w(Throwable t, String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void e(String message, Object... args) {
                System.out.println(formatString(message, args));
            }

            @Override public void e(Throwable t, String message, Object... args) {
                System.out.println(formatString(message, args));
            }
        });
    }

    @Override public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    @Override public void beforeTest(Method method) {

    }

    @Override public void prepareTest(Object test) {
        //objectGraph.inject(test);
    }

    @Override public void afterTest(Method method) {

    }
}
