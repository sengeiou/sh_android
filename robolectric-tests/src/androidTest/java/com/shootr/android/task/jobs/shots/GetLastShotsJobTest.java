package com.shootr.android.task.jobs.shots;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.shots.LatestShotsResultEvent;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricGradleTestRunner.class)
public class GetLastShotsJobTest {

    public static final Long IRRELEVANT_ID_USER = 1L;
    public GetLastShotsJob job;
    private ShotManager shotManager;
    private ShootrService service;
    private Bus bus;
    private NetworkUtil networkUtil;
    private UserManager userManager;
    private SQLiteOpenHelper openHelper;

    @Before
    public void setUp(){
        this.service = mock(ShootrService.class);
        this.shotManager = mock(ShotManager.class);
        this.userManager = mock(UserManager.class);
        this.bus = mock(Bus.class);
        this.networkUtil = mock(NetworkUtil.class);
        this.openHelper = mock(SQLiteOpenHelper.class);

        job = new GetLastShotsJob(Robolectric.application,bus,networkUtil,service,shotManager,userManager,openHelper);
    }

    @Test
    public void postOnceIfInternetConnectionIsDown() throws Throwable {
        when(networkUtil.isConnected(any(Context.class))).thenReturn(false);
        List<ShotEntity> shotEntities = new ArrayList<>();
        shotEntities.add(new ShotEntity());
        shotEntities.add(new ShotEntity());
        shotEntities.add(new ShotEntity());
        when(job.getLatestShotsFromDatabase()).thenReturn(shotEntities);
        job.init(IRRELEVANT_ID_USER);
        job.onRun();
        ArgumentCaptor<Object> eventArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(bus,atLeastOnce()).post(eventArgumentCaptor.capture());
        Object[] events = eventArgumentCaptor.getAllValues().toArray();
        assertThat(events[0]).isExactlyInstanceOf(ConnectionNotAvailableEvent.class);
        assertThat(events[1]).isExactlyInstanceOf(LatestShotsResultEvent.class);
    }

    @Test
    public void postTwiceIfInternetConnectionIsAvailableAndBothAreLatestShotsResultEvent() throws Throwable{
        when(networkUtil.isConnected(Robolectric.application)).thenReturn(true);
        job.init(IRRELEVANT_ID_USER);
        job.onRun();
        ArgumentCaptor<Object> eventArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(bus,atLeastOnce()).post(eventArgumentCaptor.capture());
        Object[] events = eventArgumentCaptor.getAllValues().toArray();
        assertThat(events[0]).isExactlyInstanceOf(LatestShotsResultEvent.class);
        assertThat(events[1]).isExactlyInstanceOf(LatestShotsResultEvent.class);
    }

    @Test
    public void postEmptyListIfUserHasNotShots() throws Throwable {
        List<ShotEntity> shotEntities = new ArrayList<>();
        when(job.getLatestShotsFromDatabase()).thenReturn(shotEntities);
        when(networkUtil.isConnected(Robolectric.application)).thenReturn(true);
        job.init(IRRELEVANT_ID_USER);
        job.onRun();
        ArgumentCaptor<LatestShotsResultEvent> eventArgumentCaptor = ArgumentCaptor.forClass(LatestShotsResultEvent.class);
        verify(bus, times(2)).post(eventArgumentCaptor.capture());
        LatestShotsResultEvent event = eventArgumentCaptor.getValue();
        assertThat(event.getResult().size()).isEqualTo(0);
    }


}
