package gm.mobi.android.task.jobs.follows;

import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.RobolectricGradleTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18) @RunWith(RobolectricGradleTestRunner.class)
public class GetUsersFollowsJobTest {

    private GolesApplication application;
    private ObjectGraph objectGraph;

    @Before
    public void setup() {
        application = (GolesApplication) Robolectric.application;
        objectGraph = application.getObjectGraph();
    }

    @Test
    public void postCommunicationErrorWhenExceptionThrownRetrievingFollowing() {
        //TODO Adrián, qué hacemos con estos tests repetidos?
    }
}
