package com.shootr.android.task.jobs.timeline;

import android.database.sqlite.SQLiteOpenHelper;
import butterknife.internal.ListenerClass;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.content.Context;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class GetWatchingPeopleNumberJobTest{

    private static final Long STATUS_WATCHING = 1L;
    private static final Long STATUS_NOT_WATCHING = 0L;
    private Bus bus;
    private WatchManager watchManager;
    private MatchManager matchManager;
    private SQLiteOpenHelper openHelper;
    private NetworkUtil networkUtil;
    public GetWatchingPeopleNumberJob job;
    private static final Long ID_MATCH = 1L;
    private static final Long ID_MATCH_2= 2L;
    private static final Long ID_MATCH_3 = 3L;

    private static final Long ID_USER = 1L;
    private static final Long ID_USER2 = 2L;
    private static final Long ID_USER3 = 3L;

    @Before public void setUp() throws IOException{
       watchManager = mock(WatchManager.class);
       matchManager = mock(MatchManager.class);
       bus = mock(Bus.class);
       networkUtil = mock(NetworkUtil.class);
       openHelper = mock(SQLiteOpenHelper.class);
       when(networkUtil.isConnected(any(Context.class))).thenReturn(true);
       job = new GetWatchingPeopleNumberJob(Robolectric.application,bus,networkUtil,openHelper,watchManager,matchManager);
    }

    @Test
    public void PostOnceAnyNumberComingFromDatabase() throws Throwable {
        when(watchManager.getPeopleWatchingInInfo()).thenReturn(any(Integer.class));
        job.onRun();
        verify(bus,times(1)).post(new WatchingPeopleNumberEvent(any(Integer.class)));
    }

    @Test
    public void returnWatchesFromMatchesInLive() throws Throwable {
        when(watchManager.getPeopleWatchingInInfo()).thenReturn(2);
        job.onRun();
        ArgumentCaptor<WatchingPeopleNumberEvent> eventArgumentCaptor = ArgumentCaptor.forClass(WatchingPeopleNumberEvent.class);

        verify(bus).post(eventArgumentCaptor.capture());
        WatchingPeopleNumberEvent event = eventArgumentCaptor.getValue();
        assertThat(event.getResult()).isEqualTo(2);
    }



}
