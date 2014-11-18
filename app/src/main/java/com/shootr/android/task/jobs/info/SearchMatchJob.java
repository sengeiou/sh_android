package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.MatchSearchResultModel;
import com.shootr.android.ui.model.mappers.MatchSearchResultModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchMatchJob extends ShootrBaseJob<SearchMatchResultEvent> {

    public static final int PRIORITY = 7;
    private ShootrService service;
    private MatchSearchResultModelMapper matchSearchResultModelMapper;
    private String queryText;
    private WatchManager watchManager;
    private FollowManager followManager;
    private SessionManager sessionManager;

    @Inject protected SearchMatchJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      MatchSearchResultModelMapper matchSearchResultModelMapper, WatchManager watchManager,
      SessionManager sessionManager, SQLiteOpenHelper openHelper, FollowManager followManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.matchSearchResultModelMapper = matchSearchResultModelMapper;
        this.watchManager = watchManager;
        this.sessionManager = sessionManager;
        this.followManager = followManager;
        this.setOpenHelper(openHelper);
    }

    public void init(String queryText) {
        this.queryText = queryText;
    }

    @Override protected void run() throws IOException, SQLException {
        List<MatchEntity> resultMatches = service.searchMatches(queryText);


        //Filter
        // My team
        MatchEntity nextMatchFromMyTeam = service.getNextMatchWhereMyFavoriteTeamPlays(sessionManager.getCurrentUser().getFavoriteTeamId());
        // My following's watches
        List<Long> followingsAndMeIds =
          followManager.getUserFollowingIdsWithOwnUser(sessionManager.getCurrentUserId());
        List<WatchEntity> watchesFromMyFollowing =
          watchManager.getWatchesNotEndedOrAdjurnedFromUsers(followingsAndMeIds);

        List<Long> alreadyAddedMatchesIds = new ArrayList<>();
        if (nextMatchFromMyTeam != null) {
            alreadyAddedMatchesIds.add(nextMatchFromMyTeam.getIdMatch());
        }
        if (watchesFromMyFollowing != null) {
            for (WatchEntity watch : watchesFromMyFollowing) {
                alreadyAddedMatchesIds.add(watch.getIdMatch());
            }
        }

        List<MatchSearchResultModel> matchModels = new ArrayList<>();
        for (MatchEntity match : resultMatches) {
            MatchSearchResultModel matchModel = matchSearchResultModelMapper.transform(match, alreadyAddedMatchesIds.contains(match.getIdMatch()));
            matchModels.add(matchModel);

        }
        postSuccessfulEvent(new SearchMatchResultEvent(matchModels));
    }

    private List<Long> getMatchesIdsFromWatches(List<WatchEntity> watchEntities) {
        List<Long> matchesIds = new ArrayList<>(watchEntities.size());
        for (WatchEntity watchEntity : watchEntities) {
            matchesIds.add(watchEntity.getIdMatch());
        }
        return matchesIds;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    @Override protected void createDatabase() {
        createReadableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        watchManager.setDataBase(db);
        followManager.setDataBase(db);
    }
}
