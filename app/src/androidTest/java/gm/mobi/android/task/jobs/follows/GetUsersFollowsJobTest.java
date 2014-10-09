package gm.mobi.android.task.jobs.follows;

import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ResultEvent;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
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
        BagdadService mockService = null;
        try {
            mockService = mock(BagdadService.class);
            when(mockService.getFollowings(anyLong(),anyLong())).thenThrow(
                ServerException.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertThat(mockService).isNotNull();

        Bus mockBus = mock(Bus.class);
        NetworkUtil mockNetworkUtil = mock(NetworkUtil.class);
        when(mockNetworkUtil.isConnected(any(Context.class))).thenReturn(true);

        GetUsersFollowsJob getUsersFollowsJob =
            new GetUsersFollowsJob(application, mockBus, mockService, mockNetworkUtil);
        getUsersFollowsJob.init(1L);
        try {
            getUsersFollowsJob.run();
        } catch (Exception e) {
        }

        ArgumentCaptor<FollowsResultEvent> argument = ArgumentCaptor.forClass(FollowsResultEvent.class);
        verify(mockBus).post(argument.capture());

        FollowsResultEvent eventPosted = argument.getValue();
        assertThat(eventPosted.getStatus()).isEqualTo(ResultEvent.STATUS_INVALID);
    }
}
