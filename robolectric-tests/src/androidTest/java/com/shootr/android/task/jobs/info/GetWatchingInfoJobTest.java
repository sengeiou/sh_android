package com.shootr.android.task.jobs.info;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJobTestAbstract;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.robolectric.Robolectric;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class GetWatchingInfoJobTest extends ShootrBaseJobTestAbstract {

    public static final long IRRELEVANT_USER_ID = 666L;
    private static final Long IRRELEVANT_DATE = 0L;
    private static final Long IRRELEVANT_MATCH_ID = 1L;

    private static final Long FAVOURITE_TEAM_ID = 1L;
    private static final Long WATCH_STATUS_WATCHING = 1L;

    public GetWatchingInfoJob job;
    private ShootrService service;
    private SessionManager sessionManager;
    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;
    private UserManager userManager;
    private FollowManager followManager;
    private SQLiteOpenHelper openHelper;
    private WatchManager watchManager;
    private MatchManager matchManager;

    private InfoListBuilderFactory infoListBuilderFactory;

    private InfoListBuilder infoListBuilder;

    @Before @Override
    public void setUp() throws IOException {
        super.setUp();
        matchModelMapper = new MatchModelMapper(Robolectric.application);
        userWatchingModelMapper = new UserWatchingModelMapper(Robolectric.application);
        service = mock(ShootrService.class);
        sessionManager = mock(SessionManager.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);
        watchManager = mock(WatchManager.class);
        matchManager = mock(MatchManager.class);
        openHelper = mock(SQLiteOpenHelper.class);
        networkUtil = mock(NetworkUtil.class);

        infoListBuilder = mock(InfoListBuilder.class);
        infoListBuilderFactory = mock(InfoListBuilderFactory.class);

        when(infoListBuilderFactory.getInfoListBuilder(sessionManager, matchModelMapper,
          userWatchingModelMapper)).thenReturn(infoListBuilder);

        Map<MatchModel, Collection<UserWatchingModel>> map = new HashMap<>();
        MatchModel match = new MatchModel();
        match.setLocalTeamId(1L);
        match.setVisitorTeamId(1L);
        match.setLocalTeamName("localTeam");
        match.setVisitorTeamName("visitorTeam");
        match.setDatetime("fecha");
        match.setLive(true);
        match.setTitle("titulo");

        Collection<UserWatchingModel> collection = new ArrayList<>();
        UserWatchingModel userWatchingModel = new UserWatchingModel();
        userWatchingModel.setUserName("wakajsd");
        userWatchingModel.setFavoriteTeamId(1L);
        userWatchingModel.setPhoto("afdsaf");
        userWatchingModel.setLive(true);
        userWatchingModel.setStatus("fadsad");
        collection.add(userWatchingModel);

        map.put(match, collection);

        when(infoListBuilder.build()).thenReturn(map);

        UserEntity currenUser = new UserEntity();
        currenUser.setFavoriteTeamId(FAVOURITE_TEAM_ID);
        currenUser.setIdUser(IRRELEVANT_USER_ID);

        when(sessionManager.getCurrentUser()).thenReturn(currenUser);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        MatchEntity match2 = new MatchEntity();
        match2.setMatchDate(new Date());

        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match2);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);
        job =
          new GetWatchingInfoJob(Robolectric.application, bus, networkUtil, service, sessionManager, matchModelMapper,
            userWatchingModelMapper, userManager, followManager, openHelper, watchManager, matchManager,
            infoListBuilderFactory);
    }

    @Override protected ShootrBaseJob getSystemUnderTest() {
        return job;
    }

    @Test
    public void resultContainsOneMatchFromMyFavoriteTeamWhenPlaysAsLocal() throws Throwable {
        MatchEntity match = new MatchEntity();
        match.setIdLocalTeam(FAVOURITE_TEAM_ID);
        match.setMatchDate(new Date());
        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match);

        job.init(false);
        job.onRun();
        verify(bus, times(2)).post(argThat(new ContainsMatchPlayedByTeamMatcher(FAVOURITE_TEAM_ID)));
    }

    @Test
    public void resultContainsOneMatchFromMyFavoriteTeamWhenPlaysAsVisitor() throws Throwable {
        MatchEntity match = new MatchEntity();
        match.setIdVisitorTeam(FAVOURITE_TEAM_ID);
        match.setMatchDate(new Date());
        List<Long> matchIds = new ArrayList<>();
        matchIds.add(FAVOURITE_TEAM_ID);

        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match);

        job.init(false);
        job.onRun();
        verify(bus, times(2)).post(argThat(new ContainsMatchPlayedByTeamMatcher(FAVOURITE_TEAM_ID)));
    }

    private WatchEntity getWatch(Long userId) {
        WatchEntity watch = new WatchEntity();
        watch.setIdUser(userId);
        watch.setIdMatch(IRRELEVANT_MATCH_ID);
        watch.setStatus(WATCH_STATUS_WATCHING);
        return watch;
    }

    @Test
    public void postOnceWhenOnlineInfoOnly() throws Throwable {
        WatchingInfoResult event = mock(WatchingInfoResult.class);
        job.init(true);
        job.onRun();
        verify(bus, atMost(1)).post(event);
    }

    @Test
    public void postTwiceIfOnlineInfoIsFalse() throws Throwable {
        WatchingInfoResult event = mock(WatchingInfoResult.class);
        job.init(false);
        job.onRun();
        verify(bus, atMost(2)).post(event);
    }

    public static class ContainsMatchPlayedByTeamMatcher extends ArgumentMatcher<WatchingInfoResult> {

        public Long teamId;

        public ContainsMatchPlayedByTeamMatcher(Long teamId) {
            this.teamId = teamId;
        }

        @Override public boolean matches(Object argument) {
            boolean res = false;
            if (argument instanceof WatchingInfoResult) {
                res = teamPlaysInMatchList((WatchingInfoResult) argument);
            }
            return res;
        }

        public boolean teamPlaysInMatchList(WatchingInfoResult event) {
            Map<MatchModel, Collection<UserWatchingModel>> resultMap = event.getResult();
            for (MatchModel matchModel : resultMap.keySet()) {
                if (teamPlaysInMatch(matchModel, teamId)) {
                    return true;
                }
            }
            return false;
        }

        public boolean teamPlaysInMatch(MatchModel matchModel, Long favouriteTeamId) {
            return favouriteTeamId.equals(matchModel.getLocalTeamId()) || favouriteTeamId.equals(
              matchModel.getVisitorTeamId());
        }
    }
}
