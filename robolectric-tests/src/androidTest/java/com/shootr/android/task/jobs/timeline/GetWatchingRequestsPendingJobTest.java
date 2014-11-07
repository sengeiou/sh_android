package com.shootr.android.task.jobs.timeline;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJobTestAbstract;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class GetWatchingRequestsPendingJobTest {

    public GetWatchingRequestsPendingJob job;
    private ShootrService service;
    private Bus bus;
    private SessionManager sessionManager;
    private MatchManager matchManager;
    private NetworkUtil networkUtil;
    private SQLiteOpenHelper openHelper;
    private WatchManager watchManager;
    private UserManager userManager;
    private WatchingRequestModelMapper watchingRequestModelMapper;

    @Before public void setUp() throws IOException {

        networkUtil = mock(NetworkUtil.class);
        service = mock(ShootrService.class);
        sessionManager = mock(SessionManager.class);
        matchManager = mock(MatchManager.class);
        watchManager = mock(WatchManager.class);
        userManager = mock(UserManager.class);
        watchingRequestModelMapper = new WatchingRequestModelMapper(Robolectric.application);
        bus = mock(Bus.class);

        UserEntity currenUser = new UserEntity();
        currenUser.setFavoriteTeamId(1L);
        currenUser.setIdUser(1L);

        when(sessionManager.getCurrentUser()).thenReturn(currenUser);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);
        openHelper = mock(SQLiteOpenHelper.class);

        job = new GetWatchingRequestsPendingJob(Robolectric.application,bus,networkUtil,openHelper,watchManager,matchManager,userManager,sessionManager,watchingRequestModelMapper);
    }

     protected ShootrBaseJob getSystemUnderTest() {
        return job;
    }

    @Test
    public void resultAtLeastOneConnectionNotAvailable() throws Throwable {
        when(networkUtil.isConnected(any(Context.class))).thenReturn(false);
        job.onRun();
        verify(bus, atLeastOnce()).post(argThat(new ShootrBaseJobTestAbstract.ConnectionNotAvailableMatcher()));
    }

    @Test
    public void resultContainsAMatchWhereMyFavoriteTeamIsGoingToPlay(){
        MatchEntity match = new MatchEntity();
        match.setIdVisitorTeam(sessionManager.getCurrentUser().getFavoriteTeamId());

        when(matchManager.getNextMatchFromTeam(sessionManager.getCurrentUserId())).thenReturn(match);

    }


}
