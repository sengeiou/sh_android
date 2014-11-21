package com.shootr.android.task.jobs.info;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.ui.model.MatchSearchResultModel;
import com.shootr.android.ui.model.mappers.MatchSearchResultModelMapper;
import com.shootr.android.util.TimeFormatter;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
public class SearchMatchJobTest {

    private static final String IRRELEVANT_SEARCH_QUERY = "search";
    public static final long IRRELEVANT_MATCH_ID = 1L;
    public static final long MY_TEAM_MATCH_ID = 2L;
    public static final long CURRENT_USER_ID = 1L;
    public static final long FOLLOWING_USER_ID = 2L;
    private static final Long MY_TEAM_ID = 10L;
    public static final ArrayList<WatchEntity> EMPTY_WATCH_LIST = new ArrayList<>(0);

    private Bus bus;
    private NetworkUtil networkUtil;
    private ShootrService service;
    private MatchSearchResultModelMapper matchSearchResultModelMapper;
    private WatchManager watchManager;
    private SessionManager sessionManager;
    private SQLiteOpenHelper sqliteOpenHelper;
    private FollowManager followManager;
    private TimeFormatter timeFormatter;
    private SearchMatchJob job;

    @Before
    public void setUp() {
        bus = mock(Bus.class);
        networkUtil = mock(NetworkUtil.class);
        service = mock(ShootrService.class);
        timeFormatter = mock(TimeFormatter.class);
        watchManager = mock(WatchManager.class);
        sessionManager = mock(SessionManager.class);
        sqliteOpenHelper = mock(SQLiteOpenHelper.class);
        followManager = mock(FollowManager.class);
        matchSearchResultModelMapper = new MatchSearchResultModelMapper(timeFormatter);

        job = new SearchMatchJob(Robolectric.application, bus, networkUtil, service, matchSearchResultModelMapper,
            watchManager, sessionManager, followManager);
        job.init(IRRELEVANT_SEARCH_QUERY);
    }

    @Test
    public void matchEnabledWhenNobodyWatching() throws Throwable {
        MatchEntity providedMatch = getIrrelevantMatch();
        List<MatchEntity> providedSearchResult = getMatchAsList(providedMatch);

        when(service.searchMatches(anyString())).thenReturn(providedSearchResult);
        when(service.getNextMatchWhereMyFavoriteTeamPlays(anyLong())).thenReturn(null);
        when(watchManager.getWatchesNotEndedOrAdjurnedFromUsers(anyList())).thenReturn(new ArrayList<WatchEntity>());

        setupSessionAndNetwork();

        job.onRun();

        List<MatchSearchResultModel> results = capureResult();

        MatchSearchResultModel obtainedResult = results.get(0);
        assertThat(obtainedResult.getIdMatch()).isEqualTo(providedMatch.getIdMatch());
        assertThat(obtainedResult.isAddedAlready()).isFalse();
    }

    @Test
    public void matchDisabledWhenFollowingIsWatching() throws Throwable {
        MatchEntity providedMatch = getIrrelevantMatch();
        List<MatchEntity> providedSearchResult = getMatchAsList(providedMatch);

        WatchEntity watchFromFollowing = getWatchFromFollowing();
        List<WatchEntity> providedFollowingMatches = getWatchAsList(watchFromFollowing);

        when(service.searchMatches(anyString())).thenReturn(providedSearchResult);
        when(service.getNextMatchWhereMyFavoriteTeamPlays(anyLong())).thenReturn(null);
        when(watchManager.getWatchesNotEndedOrAdjurnedFromUsers(anyList())).thenReturn(providedFollowingMatches);

        setupSessionAndNetwork();

        job.onRun();

        List<MatchSearchResultModel> results = capureResult();

        MatchSearchResultModel obtainedResult = results.get(0);
        assertThat(obtainedResult.getIdMatch()).isEqualTo(providedMatch.getIdMatch());
        assertThat(obtainedResult.isAddedAlready()).isTrue();
    }

    @Test
    public void matchEnabledWhenFollowingWatchingAndDeleted() throws Throwable {
        MatchEntity providedMatch = getIrrelevantMatch();
        List<MatchEntity> providedSearchResult = getMatchAsList(providedMatch);

        WatchEntity watchFromFollowing = getWatchFromFollowing();
        WatchEntity myWatchNotVisible = getMyWatchNotVisible();
        List<WatchEntity> providedWatchesFromFollowingAndMe = getWatchAsList(watchFromFollowing, myWatchNotVisible);

        when(service.searchMatches(anyString())).thenReturn(providedSearchResult);
        when(service.getNextMatchWhereMyFavoriteTeamPlays(anyLong())).thenReturn(null);
        when(watchManager.getWatchesNotEndedOrAdjurnedFromUsers(anyList())).thenReturn(providedWatchesFromFollowingAndMe);

        setupSessionAndNetwork();

        job.onRun();

        List<MatchSearchResultModel> results = capureResult();

        MatchSearchResultModel obtainedResult = results.get(0);
        assertThat(obtainedResult.getIdMatch()).isEqualTo(providedMatch.getIdMatch());
        assertThat(obtainedResult.isAddedAlready()).isFalse();
    }

    @Test
    public void matchDisabledWhenMyTeamNextMatchWithoutWatching() throws Throwable {
        MatchEntity myTeamNextMatch = getMyTeamNextMatch();
        List<MatchEntity> providedSearchResult = getMatchAsList(myTeamNextMatch);

        when(service.searchMatches(anyString())).thenReturn(providedSearchResult);
        when(service.getNextMatchWhereMyFavoriteTeamPlays(anyLong())).thenReturn(myTeamNextMatch);
        when(watchManager.getWatchesNotEndedOrAdjurnedFromUsers(anyList())).thenReturn(EMPTY_WATCH_LIST);

        setupSessionAndNetwork();

        job.onRun();

        List<MatchSearchResultModel> results = capureResult();

        MatchSearchResultModel obtainedResult = results.get(0);
        assertThat(obtainedResult.getIdMatch()).isEqualTo(myTeamNextMatch.getIdMatch());
        assertThat(obtainedResult.isAddedAlready()).isTrue();
    }

    @Test
    public void matchEnabledWhenMyTeamNextMatchDeleted() throws Throwable {
        MatchEntity myTeamNextMatch = getMyTeamNextMatch();
        List<MatchEntity> providedSearchResult = getMatchAsList(myTeamNextMatch);

        when(service.searchMatches(anyString())).thenReturn(providedSearchResult);
        when(service.getNextMatchWhereMyFavoriteTeamPlays(anyLong())).thenReturn(myTeamNextMatch);
        when(watchManager.getWatchesNotEndedOrAdjurnedFromUsers(anyList())).thenReturn(EMPTY_WATCH_LIST);
        when(watchManager.getWatchByKeys(CURRENT_USER_ID, MY_TEAM_MATCH_ID)).thenReturn(getMyTeamWatchNotVisible());

        setupSessionAndNetwork();

        job.onRun();

        List<MatchSearchResultModel> results = capureResult();

        MatchSearchResultModel obtainedResult = results.get(0);
        assertThat(obtainedResult.getIdMatch()).isEqualTo(myTeamNextMatch.getIdMatch());
        assertThat(obtainedResult.isAddedAlready()).isFalse();
    }

    private WatchEntity getMyTeamWatchNotVisible() {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(CURRENT_USER_ID);
        watchEntity.setIdMatch(MY_TEAM_MATCH_ID);
        watchEntity.setVisible(false);
        watchEntity.setStatus(WatchEntity.STATUS_REJECT);
        return watchEntity;
    }

    private void setupSessionAndNetwork() {
        when(sessionManager.getCurrentUser()).thenReturn(getCurrentUser());
        when(sessionManager.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);
    }

    private List<MatchSearchResultModel> capureResult() {
        ArgumentCaptor<SearchMatchResultEvent> searchArgumentCaptor = ArgumentCaptor.forClass(SearchMatchResultEvent.class);
        verify(bus).post(searchArgumentCaptor.capture());

        SearchMatchResultEvent searchEvent = searchArgumentCaptor.getValue();
        return searchEvent.getResult();
    }

    private List<WatchEntity> getWatchAsList(WatchEntity... watches) {
        return Arrays.asList(watches);
    }

    private List<MatchEntity> getMatchAsList(MatchEntity providedMatch) {
        List<MatchEntity> providedSearchResult = new ArrayList<>();
        providedSearchResult.add(providedMatch);
        return providedSearchResult;
    }

    private MatchEntity getIrrelevantMatch() {
        MatchEntity match = new MatchEntity();
        match.setIdMatch(IRRELEVANT_MATCH_ID);
        match.setMatchDate(new Date());
        match.setStatus(0L);
        match.setIdLocalTeam(1L);
        match.setIdVisitorTeam(2L);
        return match;
    }

    private MatchEntity getMyTeamNextMatch() {
        MatchEntity match = new MatchEntity();
        match.setIdMatch(MY_TEAM_MATCH_ID);
        match.setMatchDate(new Date());
        match.setStatus(0L);
        match.setIdLocalTeam(MY_TEAM_ID);
        match.setIdVisitorTeam(2L);
        return match;
    }

    private UserEntity getCurrentUser() {
        UserEntity user = new UserEntity();
        user.setIdUser(CURRENT_USER_ID);
        user.setFavoriteTeamId(MY_TEAM_ID);
        return user;
    }

    public WatchEntity getMyWatchNotVisible() {
        return getMyWatch(false);
    }

    private WatchEntity getWatchFromFollowing() {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(FOLLOWING_USER_ID);
        watchEntity.setIdMatch(IRRELEVANT_MATCH_ID);
        return watchEntity;
    }

    private WatchEntity getMyWatch(boolean visible) {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdUser(CURRENT_USER_ID);
        watchEntity.setIdMatch(IRRELEVANT_MATCH_ID);
        watchEntity.setVisible(visible);
        watchEntity.setStatus(WatchEntity.STATUS_REJECT);
        return watchEntity;
    }
}
