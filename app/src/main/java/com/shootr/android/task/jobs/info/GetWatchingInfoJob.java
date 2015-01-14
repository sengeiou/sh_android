package com.shootr.android.task.jobs.info;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.User;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import com.squareup.otto.Bus;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class GetWatchingInfoJob extends ShootrBaseJob<WatchingInfoResult> {

    private static final int PRIORITY = 5;
    private ShootrService service;
    private SessionRepository sessionRepository;
    private MatchModelMapper matchModelMapper;
    private UserEntityWatchingModelMapper userWatchingModelMapper;
    private UserManager userManager;
    private WatchManager watchManager;
    private MatchManager matchManager;
    private FollowManager followManager;
    private InfoListBuilderFactory infoListBuilderFactory;
    private boolean postOnlineInfoOnly;
    private UserEntityMapper userEntityMapper;

    @Inject public GetWatchingInfoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      SessionRepository sessionRepository, MatchModelMapper matchModelMapper, UserEntityWatchingModelMapper userWatchingModelMapper,
      UserManager userManager, FollowManager followManager, WatchManager watchManager, MatchManager matchManager,
      InfoListBuilderFactory infoListBuilderFactory, UserEntityMapper userEntityMapper) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.service = service;
        this.sessionRepository = sessionRepository;
        this.matchModelMapper = matchModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.userManager = userManager;
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        this.followManager = followManager;
        this.infoListBuilderFactory = infoListBuilderFactory;
        this.userEntityMapper = userEntityMapper;
    }

    public void init(boolean postOnlineInfoOnly) {
        this.postOnlineInfoOnly = postOnlineInfoOnly;
    }

    @Override protected void run() throws SQLException, IOException {
        if(!postOnlineInfoOnly) {
            Map<MatchModel, Collection<UserWatchingModel>> infoListOffline = obtainInfoList(false);
            if (infoListOffline != null) {
                Timber.d("Sending watching list offline");
                postSuccessfulEvent(new WatchingInfoResult(infoListOffline));
            }
        }
        if (hasInternetConnection()) {
            Timber.d("Sending watching list online");
            Map<MatchModel, Collection<UserWatchingModel>> infoListOnline = obtainInfoList(true);
            if (infoListOnline != null) {
                postSuccessfulEvent(new WatchingInfoResult(infoListOnline));
            }
        }
    }

    private Map<MatchModel, Collection<UserWatchingModel>> obtainInfoList(boolean useOnlineData)
      throws IOException, SQLException {
        InfoListBuilder infoListBuilder = infoListBuilderFactory.getInfoListBuilder(sessionRepository, matchModelMapper,userWatchingModelMapper,
          userEntityMapper);
        List<WatchEntity> watches = getWatches(useOnlineData);
        if (watches != null && !watches.isEmpty()) {
            infoListBuilder.setWatches(watches);
            infoListBuilder.provideMatches(getMatches(infoListBuilder.getMatchIds(), useOnlineData));
            infoListBuilder.provideUsers(getUsersFromDatabase(infoListBuilder.getUserIds()));
        }

        MatchEntity nextMatchFromMyTeam = getNextMatchWhereMyFavoriteTeamPlays(useOnlineData);
        if (nextMatchFromMyTeam != null) {
            infoListBuilder.putMyTeamMatch(nextMatchFromMyTeam);
        }

        return infoListBuilder.build();
    }

    private MatchEntity getNextMatchWhereMyFavoriteTeamPlays(boolean useOnlineData) throws IOException {
        MatchEntity nextMatch = matchManager.getNextMatchFromTeam(getFavoriteTeamId());
        if (useOnlineData) {
            MatchEntity nextMatchFromServer = service.getNextMatchWhereMyFavoriteTeamPlays(getFavoriteTeamId());
            if (nextMatchFromServer != null) {
                nextMatch = nextMatchFromServer;
                matchManager.saveMatch(nextMatchFromServer);
            }
        }
        return nextMatch;
    }

    private List<MatchEntity> getMatches(List<Long> matchIds, boolean useOnlineData) throws IOException {
        if (matchIds.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<MatchEntity> matches = matchManager.getMatchesByIds(matchIds);
        if (useOnlineData) {
            List<MatchEntity> matchesFromServer = service.getMatchesByIds(matchIds);
            if (matchesFromServer != null) {
                matches = matchesFromServer;
                saveMatchesInDatabase(matchesFromServer);
            }
        }
        return matches;
    }

    private void saveMatchesInDatabase(List<MatchEntity> matchesToSave) {
        matchManager.deleteAllMatches();
        matchManager.saveMatches(matchesToSave);
    }

    private List<UserEntity> getUsersFromDatabase(List<Long> usersIds) {
        return userManager.getUsersByIds(usersIds);
    }

    private List<WatchEntity> getWatches(boolean useOnlineData) throws SQLException, IOException {
        List<WatchEntity> watches = getWatchesFromDatabase();
            if (useOnlineData) {
            List<WatchEntity> newWatchesFromServer = service.getWatchesFromUsers(getIdsFromMyFollowingAndMe(), sessionRepository
              .getCurrentUserId());
            if (newWatchesFromServer != null) {
                watches = newWatchesFromServer;
                replaceWatchesInDatabase(newWatchesFromServer);
            }
        }
        return watches;
    }

    public List<WatchEntity> getWatchesFromDatabase() throws SQLException {
        return watchManager.getWatchesNotEndedOrAdjurnedFromUsers(getIdsFromMyFollowingAndMe());
    }

    private void replaceWatchesInDatabase(List<WatchEntity> newWatchesFromServer) {
        watchManager.deleteAllWatches();
        watchManager.saveWatches(newWatchesFromServer);
    }

    public List<Long> getIdsFromMyFollowingAndMe() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(sessionRepository.getCurrentUserId());
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public Long getFavoriteTeamId() {
        User currentUser = sessionRepository.getCurrentUser();
        return currentUser.getFavoriteTeamId();
    }
}
