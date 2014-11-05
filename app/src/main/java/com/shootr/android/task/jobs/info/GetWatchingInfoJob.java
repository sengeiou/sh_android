package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
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
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class GetWatchingInfoJob extends ShootrBaseJob<WatchingInfoResult> {

    private static final int PRIORITY = 7;
    private ShootrService service;
    private SessionManager sessionManager;
    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;
    private UserManager userManager;
    private WatchManager watchManager;
    private MatchManager matchManager;
    private FollowManager followManager;
    private boolean postOnlineInfoOnly;

    @Inject public GetWatchingInfoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      SessionManager sessionManager, MatchModelMapper matchModelMapper, UserWatchingModelMapper userWatchingModelMapper,
      UserManager userManager, FollowManager followManager, SQLiteOpenHelper openHelper, WatchManager watchManager,
      MatchManager matchManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.sessionManager = sessionManager;
        this.matchModelMapper = matchModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.userManager = userManager;
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        this.followManager = followManager;
        this.setOpenHelper(openHelper);
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
        InfoListBuilder infoListBuilder =
          new InfoListBuilder(sessionManager.getCurrentUser(), matchModelMapper, userWatchingModelMapper);
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
        List<MatchEntity> matches = matchManager.getMatchesByIds(matchIds);
        if (useOnlineData) {
            List<MatchEntity> matchesFromServer = service.getMatchesByIds(matchIds);
            if (matchesFromServer != null && !matchesFromServer.isEmpty()) {
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
            List<WatchEntity> newWatchesFromServer = service.getWatchesFromUsers(getIdsFromMyFollowingAndMe());
            if (newWatchesFromServer != null && !newWatchesFromServer.isEmpty()) {
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
        watchManager.deleteAllWatches(sessionManager.getCurrentUserId());
        watchManager.saveWatches(newWatchesFromServer);
    }

    public List<Long> getIdsFromMyFollowingAndMe() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(sessionManager.getCurrentUserId());
    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
        watchManager.setDataBase(db);
        matchManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public Long getFavoriteTeamId() {
        UserEntity currentUser = sessionManager.getCurrentUser();
        return currentUser.getFavoriteTeamId();
    }
}
