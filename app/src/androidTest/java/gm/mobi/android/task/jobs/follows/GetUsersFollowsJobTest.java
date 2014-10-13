package gm.mobi.android.task.jobs.follows;

import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18) @RunWith(RobolectricTestRunner.class)
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
