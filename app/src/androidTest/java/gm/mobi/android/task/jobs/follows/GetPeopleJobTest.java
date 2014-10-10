package gm.mobi.android.task.jobs.follows;

import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GetPeopleJobTest {

    @Test
    public void postErrorWhenCallToServiceFails() throws Throwable{

        Bus bus = mock(Bus.class);
        BagdadService service = mock(BagdadService.class);
        when(service.getFollowings(anyLong(),anyLong())).thenThrow(new ServerException());

        GetPeopleJob getPeopleJob = new GetPeopleJob(Robolectric.application, bus, service, null, null, null);

        getPeopleJob.setBus(bus);
        getPeopleJob.setService(service);
        getPeopleJob.init();
        getPeopleJob.onRun();

        verify(bus).post(any(CommunicationErrorEvent.class));

    }

    @Test
    public void postConnectionNotAvailableEventWhenConnectionNotAvailable() throws Throwable {
        NetworkUtil networkUtil = mock(NetworkUtil.class);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(false);

        BagdadService service = mock(BagdadService.class);
        when(service.getFollowings(anyLong(),anyLong())).thenReturn(new ArrayList<User>());

        Bus bus = mock(Bus.class);

        GetPeopleJob getPeopleJob = new GetPeopleJob(Robolectric.application, bus, service, networkUtil, null, null);

        getPeopleJob.setNetworkUtil(networkUtil);
        getPeopleJob.setService(service);
        getPeopleJob.setBus(bus);

        getPeopleJob.init();
        getPeopleJob.onRun();

        ArgumentCaptor<ConnectionNotAvailableEvent> eventArgumentCaptor =
          ArgumentCaptor.forClass(ConnectionNotAvailableEvent.class);

        verify(bus).post(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue()).isInstanceOf(ConnectionNotAvailableEvent.class);
    }
}
