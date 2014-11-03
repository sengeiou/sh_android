package com.shootr.android.task.jobs.info;

import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJobTestAbstract;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class GetWatchingInfoJobTest extends ShootrBaseJobTestAbstract {

    public static final long IRRELEVANT_USER_ID = 666L;
    private static final Long IRRELEVANT_DATE = 0L;
    private static final Long IRRELEVANT_MATCH_ID = 1L;

    private static final Long FAVOURITE_TEAM_ID = 1L;
    private static final Long WATCH_STATUS_WATCHING = 1L;

    private GetWatchingInfoJob job;
    private ShootrService service;
    private SessionManager sessionManager;
    private MatchModelMapper matchModelMapper;

    @Before @Override
    public void setUp() throws IOException {
        super.setUp();
        service = mock(ShootrService.class);
        sessionManager = mock(SessionManager.class);
        //matchModelMapper = mock(MatchModelMapper.class); //TODO adriáaaaaaan!!!!
        matchModelMapper = new MatchModelMapper();

        UserEntity currenUser = new UserEntity();
        currenUser.setFavoriteTeamId(FAVOURITE_TEAM_ID);
        currenUser.setIdUser(IRRELEVANT_USER_ID);
        when(sessionManager.getCurrentUser()).thenReturn(currenUser);

        MatchEntity match = new MatchEntity();
        match.setMatchDate(new Date());
        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match);

        /*job =
          new GetWatchingInfoJob(Robolectric.application, bus, networkUtil, service, sessionManager, matchModelMapper);*/
    }

    @Override protected ShootrBaseJob getSystemUnderTest() {
        return job;
    }

    @Test @Ignore
    public void resultContainsOneMatchFromMyFavoriteTeamWhenPlaysAsLocal() throws Throwable {
        MatchEntity match = new MatchEntity();
        match.setIdLocalTeam(FAVOURITE_TEAM_ID);
        match.setMatchDate(new Date());
        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match);

        job.init();
        job.onRun();

        verify(bus).post(argThat(new ContainsMatchPlayedByTeamMatcher(FAVOURITE_TEAM_ID)));
    }

    @Test @Ignore
    public void resultContainsOneMatchFromMyFavoriteTeamWhenPlaysAsVisitor() throws Throwable {
        MatchEntity match = new MatchEntity();
        match.setIdVisitorTeam(FAVOURITE_TEAM_ID);
        match.setMatchDate(new Date());
        when(service.getNextMatchWhereMyFavoriteTeamPlays(eq(FAVOURITE_TEAM_ID))).thenReturn(match);

        job.init();
        job.onRun();

        verify(bus).post(argThat(new ContainsMatchPlayedByTeamMatcher(FAVOURITE_TEAM_ID)));
    }

    @Test @Ignore
    public void resultContainsMatchesThatIAmWatching() throws Throwable {
        MatchModel matchWatching1 = new MatchModel();
        matchWatching1.setIdMatch(1L);
        MatchModel matchWatching2 = new MatchModel();
        matchWatching2.setIdMatch(2L);
        MatchModel matchWatching3 = new MatchModel();
        matchWatching3.setIdMatch(3L);
        MatchModel matchNotWatching4 = new MatchModel();
        matchNotWatching4.setIdMatch(4L);

        MatchModel[] matchesIAmWatching = new MatchModel[] {
          matchWatching1, matchWatching2, matchWatching3,
        };
        //TODO TEST
        //when(service.getWatchesFromUsers(anyList(), anyLong())).thenReturn()
        //when(service.getNextMatchWhereMyFavoriteTeamPlays())

        job.init();
        job.onRun();

        ArgumentCaptor<WatchingInfoResult> argumentCaptor = ArgumentCaptor.forClass(WatchingInfoResult.class);
        verify(bus).post(argumentCaptor.capture());

        //Map<MatchModel, List<UserWatchingModel>> result = argumentCaptor.getValue().getResult();
        //assertThat(result).containsKeys(matchesIAmWatching);
    }

    @Test @Ignore
    public void retrieveWatchesFromUsersDoesSomething() throws IOException {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        userIds.add(3L);
        userIds.add(4L);
        userIds.add(5L);
        userIds.add(6L);

        List<WatchEntity> expectedWatches = new ArrayList<>();
        expectedWatches.add(getWatch(1L));
        expectedWatches.add(getWatch(3L));
        expectedWatches.add(getWatch(4L));
        expectedWatches.add(getWatch(6L));

        when(service.getWatchesFromUsers(eq(userIds), eq(IRRELEVANT_DATE))).thenReturn(expectedWatches);
        job.init();
        //List<WatchEntity> watches = job.getWatchesFromUsers(userIds);
    }

    private WatchEntity getWatch(Long userId) {
        WatchEntity watch = new WatchEntity();
        watch.setIdUser(userId);
        watch.setIdMatch(IRRELEVANT_MATCH_ID);
        watch.setStatus(WATCH_STATUS_WATCHING);
        return watch;
    }

    public static class ContainsMatchPlayedByTeamMatcher extends ArgumentMatcher<Object> {

        private Long teamId;

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

        private boolean teamPlaysInMatchList(WatchingInfoResult event) {
           /* Map<MatchModel, List<UserWatchingModel>> resultMap = event.getResult();
            for (MatchModel matchModel : resultMap.keySet()) {
                if (teamPlaysInMatch(matchModel, teamId)) {
                    return true;
                }
            }*/
            return false;
        }

        private boolean teamPlaysInMatch(MatchModel matchModel, Long favouriteTeamId) {
            return favouriteTeamId.equals(matchModel.getLocalTeamId()) || favouriteTeamId.equals(
              matchModel.getVisitorTeamId());
        }
    }
}
