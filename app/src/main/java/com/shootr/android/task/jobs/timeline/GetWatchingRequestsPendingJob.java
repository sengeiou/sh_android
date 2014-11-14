package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LongSparseArray;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.task.events.timeline.WatchInfoBuilder;
import com.shootr.android.task.events.timeline.WatchingRequestPendingEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.WatchingRequestModel;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class GetWatchingRequestsPendingJob extends ShootrBaseJob<WatchingRequestPendingEvent> {

    private static final long WATCHING_REQUEST_THRESHOLD_MILLIS = 15 * 60 * 1000;
    private static final int PRIORITY = 9;
    private WatchManager watchManager;
    private MatchManager matchManager;
    private UserManager userManager;
    private SessionManager sessionManager;
    private WatchingRequestModelMapper watchingRequestModelMapper;


    @Inject public GetWatchingRequestsPendingJob(Application application, Bus bus, NetworkUtil networkUtil,
      SQLiteOpenHelper openHelper, WatchManager watchManager, MatchManager matchManager, UserManager userManager,
      SessionManager sessionManager, WatchingRequestModelMapper watchingRequestModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
        this.watchingRequestModelMapper = watchingRequestModelMapper;
        setOpenHelper(openHelper);
    }

    @Override protected void run() throws SQLException, IOException {
        Set<WatchingRequestModel> watchingRequestModelResultCollection = new HashSet<>();

        WatchingRequestModel watchingRequestForMyTeam = getWatchingRequestForMyTeam();
        if (watchingRequestForMyTeam != null) {
            watchingRequestModelResultCollection.add(watchingRequestForMyTeam);
        }

        List<WatchingRequestModel> watchingRequestsIamNotWatching =
          getWatchingRequestsForMatchesThatMyFollowingAreWatchingAndIAmNot();

        if (watchingRequestsIamNotWatching != null) {
            watchingRequestModelResultCollection.addAll(watchingRequestsIamNotWatching);
        }

        postSuccessfulEvent(new WatchingRequestPendingEvent(new ArrayList<>(watchingRequestModelResultCollection)));
    }

    private List<WatchingRequestModel> getWatchingRequestsForMatchesThatMyFollowingAreWatchingAndIAmNot() {
        List<WatchEntity> watchesWhereIAmNot = getWatchesWhereIAmNot();
        List<Long> matchesIds = new ArrayList<>();
        List<Long> usersIds = new ArrayList<>();
        List<WatchingRequestModel> watchingRequestModels = new ArrayList<>();
        if(!watchesWhereIAmNot.isEmpty()){
            for (WatchEntity watchEntity : watchesWhereIAmNot) {
                matchesIds.add(watchEntity.getIdMatch());
                usersIds.add(watchEntity.getIdUser());
            }
            List<MatchEntity> matchesByIds = matchManager.getMatchesByIds(matchesIds);
            List<MatchEntity> matches = getRightMatchesByTime(matchesByIds);
            List<UserEntity> usersByIds = userManager.getUsersByIds(usersIds);

            WatchInfoBuilder watchInfoBuilder = new WatchInfoBuilder();
            watchInfoBuilder.setWatches(watchesWhereIAmNot);
            watchInfoBuilder.provideMatches(matches);
            watchInfoBuilder.provideUsers(usersByIds);

            Map<MatchEntity, Collection<UserEntity>> mapWatchInfo = watchInfoBuilder.build();
            List<UserEntity> userEntities;
            for(MatchEntity match:mapWatchInfo.keySet()){
                userEntities = new ArrayList<>(mapWatchInfo.get(match));
                WatchingRequestModel watchingRequestModel =  watchingRequestModelMapper.toWatchingRequestModel(match,userEntities);
                watchingRequestModels.add(watchingRequestModel);
            }
        }

        return watchingRequestModels;
    }

    private List<MatchEntity> getRightMatchesByTime(List<MatchEntity> matches) {
        List<MatchEntity> matchesToReturn = new ArrayList<>();
        for(MatchEntity m:matches){
            if(isInWatchingRequestThreshold(m)){
                matchesToReturn.add(m);
            }
        }
        return matchesToReturn;
    }


    private List<WatchEntity> getWatchesWhereIAmNot() {
        return watchManager.getWatchesWhereUserNot(sessionManager.getCurrentUserId());
    }

    private WatchingRequestModel getWatchingRequestForMyTeam() {
        MatchEntity myTeamNextMatch = getMyTeamMatch();
        if (myTeamNextMatch != null && isInWatchingRequestThreshold(myTeamNextMatch) && isCurrentUserAnswerPending(myTeamNextMatch)) {
            List<UserEntity> usersWatchingMyTeamNextMatch = getUsersWatchingMatch(myTeamNextMatch);
            return watchingRequestModelMapper.toWatchingRequestModel(myTeamNextMatch, usersWatchingMyTeamNextMatch);
        }
        return null;
    }

    private boolean isCurrentUserAnswerPending(MatchEntity myTeamNextMatch) {
        WatchEntity watchFromUserForItsMatch = watchManager.getWatchByMatchAndUser(myTeamNextMatch.getIdMatch(), sessionManager.getCurrentUserId());
        return watchFromUserForItsMatch == null;
    }

    private boolean isInWatchingRequestThreshold(MatchEntity matchEntity) {
        long currentTime = System.currentTimeMillis();
        long matchTime = matchEntity.getMatchDate().getTime();
        long matchTimethreshold = matchTime - WATCHING_REQUEST_THRESHOLD_MILLIS;
        return currentTime >= matchTimethreshold;
    }

    private List<UserEntity> getUsersWatchingMatch(MatchEntity matchEntity) {
        List<WatchEntity> watches = watchManager.getWatchesByMatch(matchEntity.getIdMatch());
        List<Long> userIds = new ArrayList<>();
        for(WatchEntity watch : watches){
            userIds.add(watch.getIdUser());
        }
        return userManager.getUsersByIds(userIds);
    }

    private MatchEntity getMyTeamMatch() {
        return matchManager.getNextMatchFromTeam(sessionManager.getCurrentUser().getFavoriteTeamId());
    }

    @Override protected void createDatabase() {
        createReadableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        watchManager.setDataBase(db);
        matchManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
