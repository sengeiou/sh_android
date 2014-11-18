package com.shootr.android.task.jobs.info;

import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.util.Date;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class DeleteMatchOfflineJobTest {

    private static final Long MATCH_ID = 1L;
    private static final Long CURRENT_USER_ID = 2L;
    public static final int INITIAL_REVISION = 0;
    public static final int DAY_IN_MILLIS = 24*60*60*1000;
    public static final WatchEntity NULL_WATCH_ENTITY = null;
    private Bus bus;
    private NetworkUtil networkUtil;
    private SessionManager sessionManager;
    private SQLiteOpenHelper sqliteOpenHelper;
    private WatchManager watchManager;
    private TimeUtils timeUtils;
    private Date now;
    private Date past;
    private DeleteMatchOfflineJob job;

    @Before
    public void setUp() {
        bus = mock(Bus.class);
        networkUtil = mock(NetworkUtil.class);
        sessionManager = mock(SessionManager.class);
        sqliteOpenHelper = mock(SQLiteOpenHelper.class);
        watchManager = mock(WatchManager.class);
        timeUtils = mock(TimeUtils.class);

        now = new Date();
        past = new Date(now.getTime() - DAY_IN_MILLIS);

        job = new DeleteMatchOfflineJob(Robolectric.application, bus, networkUtil, sessionManager, sqliteOpenHelper,
            watchManager, timeUtils);
        job.init(MATCH_ID);
    }

    @Test
    public void testExistingEntityIsUpdatedWhenMatchDeleted() throws Throwable {
        applyDefaultMockConfiguration();
        when(watchManager.getWatchByKeys(CURRENT_USER_ID, MATCH_ID)).thenReturn(getExistingWatch());

        job.onRun();

        ArgumentCaptor<WatchEntity> watchCaptor = ArgumentCaptor.forClass(WatchEntity.class);
        verify(watchManager).createUpdateWatch(watchCaptor.capture());

        WatchEntity capturedWatch = watchCaptor.getValue();
        assertThat(capturedWatch.getCsysModified()).isAfter(capturedWatch.getCsysBirth());
        assertThat(capturedWatch.getCsysRevision()).isEqualTo(INITIAL_REVISION + 1);
        assertThat(capturedWatch.getCsysSynchronized()).isEqualTo("U");
    }

    @Test
    public void testNewEntityIsCreatedIfNotExistsWhenMatchDeleted() throws Throwable {
        applyDefaultMockConfiguration();
        when(watchManager.getWatchByKeys(anyLong(), anyLong())).thenReturn(NULL_WATCH_ENTITY);

        job.onRun();

        ArgumentCaptor<WatchEntity> watchCaptor = ArgumentCaptor.forClass(WatchEntity.class);
        verify(watchManager).createUpdateWatch(watchCaptor.capture());

        WatchEntity capturedWatch = watchCaptor.getValue();
        assertThat(capturedWatch.getCsysModified()).isInSameSecondAs(now);
        assertThat(capturedWatch.getCsysBirth()).isInSameSecondAs(now);
        assertThat(capturedWatch.getCsysRevision()).isEqualTo(INITIAL_REVISION);
        assertThat(capturedWatch.getCsysSynchronized()).isEqualTo("N");
        assertThat(capturedWatch.getIdUser()).isEqualTo(CURRENT_USER_ID);
        assertThat(capturedWatch.getIdMatch()).isEqualTo(MATCH_ID);
    }

    @Test
    public void watchIsNotVisibleAndStatusIsRejectedWhenNotWatching() throws Throwable {
        applyDefaultMockConfiguration();
        when(watchManager.getWatchByKeys(anyLong(), anyLong())).thenReturn(getNotWatchingWatch());

        job.onRun();

        ArgumentCaptor<WatchEntity> watchCaptor = ArgumentCaptor.forClass(WatchEntity.class);
        verify(watchManager).createUpdateWatch(watchCaptor.capture());

        WatchEntity capturedWatch = watchCaptor.getValue();
        assertThat(capturedWatch.getVisible()).isFalse();
        assertThat(capturedWatch.getStatus()).isEqualTo(WatchEntity.STATUS_REJECT);
    }

    @Test
    public void watchIsNotVisibleAndStatusIsRejectedWhenWatching() throws Throwable {
        applyDefaultMockConfiguration();
        when(watchManager.getWatchByKeys(anyLong(), anyLong())).thenReturn(getWatchingWatch());

        job.onRun();

        ArgumentCaptor<WatchEntity> watchCaptor = ArgumentCaptor.forClass(WatchEntity.class);
        verify(watchManager).createUpdateWatch(watchCaptor.capture());

        WatchEntity capturedWatch = watchCaptor.getValue();
        assertThat(capturedWatch.getVisible()).isFalse();
        assertThat(capturedWatch.getStatus()).isEqualTo(WatchEntity.STATUS_REJECT);
    }

    private void applyDefaultMockConfiguration() {
        when(sessionManager.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(timeUtils.getCurrentTime()).thenReturn(now.getTime());
    }

    private WatchEntity getWatchingWatch() {
        WatchEntity existingWatch = getExistingWatch();
        existingWatch.setStatus(WatchEntity.STATUS_WATCHING);
        return existingWatch;
    }

    private WatchEntity getNotWatchingWatch() {
        WatchEntity existingWatch = getExistingWatch();
        existingWatch.setStatus(WatchEntity.STATUS_DEFAULT);
        return existingWatch;
    }

    private WatchEntity getExistingWatch() {
        WatchEntity existingWatch = new WatchEntity();
        existingWatch.setIdMatch(MATCH_ID);
        existingWatch.setIdUser(CURRENT_USER_ID);
        existingWatch.setStatus(WatchEntity.STATUS_DEFAULT);
        existingWatch.setVisible(true);
        existingWatch.setCsysBirth(past);
        existingWatch.setCsysModified(past);
        existingWatch.setCsysRevision(INITIAL_REVISION);
        existingWatch.setCsysSynchronized("S");
        return existingWatch;
    }
}
