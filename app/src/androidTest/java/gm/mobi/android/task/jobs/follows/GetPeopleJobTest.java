package gm.mobi.android.task.jobs.follows;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GetPeopleJobTest {

    public static final long CURRENT_USER_ID =1L;

    @Test
    public void postErrorWhenCallToServiceFails() throws Throwable{

        Bus bus = mock(Bus.class);
        BagdadService service = mock(BagdadService.class);
        when(service.getFollowing(anyLong(), anyLong())).thenThrow(new ServerException());

        NetworkUtil networkUtil = mock(NetworkUtil.class);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        SQLiteOpenHelper openHelper = mock(SQLiteOpenHelper.class);
        UserManager userManager = mock(UserManager.class);
        FollowManager followManager = mock(FollowManager.class);

        GetPeopleJob getPeopleJob = new GetPeopleJob(Robolectric.application, bus, service, networkUtil, openHelper, userManager, followManager);
        getPeopleJob.init(CURRENT_USER_ID);
        getPeopleJob.onRun();

        verify(bus).post(any(CommunicationErrorEvent.class));

    }

    @Test
    public void postConnectionNotAvailableEventWhenConnectionNotAvailable() throws Throwable {
        NetworkUtil networkUtil = mock(NetworkUtil.class);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(false);

        BagdadService service = mock(BagdadService.class);
        when(service.getFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<User>());

        Bus bus = mock(Bus.class);

        SQLiteOpenHelper openHelper = mock(SQLiteOpenHelper.class);
        UserManager userManager = mock(UserManager.class);
        FollowManager followManager = mock(FollowManager.class);

        GetPeopleJob getPeopleJob = new GetPeopleJob(Robolectric.application, bus, service, networkUtil, openHelper, userManager, followManager);
        getPeopleJob.init(CURRENT_USER_ID);
        getPeopleJob.onRun();

        ArgumentCaptor<ConnectionNotAvailableEvent> eventArgumentCaptor =
          ArgumentCaptor.forClass(ConnectionNotAvailableEvent.class);

        verify(bus, atLeastOnce()).post(eventArgumentCaptor.capture());

        boolean receivedEvent = false;
        for (Object event : eventArgumentCaptor.getAllValues()) {
            if (event instanceof ConnectionNotAvailableEvent) {
                receivedEvent = true;
                break;
            }
        }
        assertThat(receivedEvent).isTrue();
        //TODO Adriáaaaaaaan!
        //assertThat(eventArgumentCaptor.getValue()).isInstanceOf(ConnectionNotAvailableEvent.class);
    }
}
