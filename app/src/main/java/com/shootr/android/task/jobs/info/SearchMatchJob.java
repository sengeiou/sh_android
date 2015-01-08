package com.shootr.android.task.jobs.info;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchSearchResultModel;
import com.shootr.android.ui.model.mappers.MatchSearchResultModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class SearchMatchJob extends ShootrBaseJob<SearchMatchResultEvent> {

    public static final int PRIORITY = 7;
    private ShootrService service;
    private MatchSearchResultModelMapper matchSearchResultModelMapper;
    private String queryText;
    private WatchManager watchManager;
    private FollowManager followManager;
    private SessionRepository sessionRepository;
    private UserEntityMapper userEntityMapper;

    @Inject protected SearchMatchJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      MatchSearchResultModelMapper matchSearchResultModelMapper, WatchManager watchManager,
      SessionRepository sessionRepository, FollowManager followManager, UserEntityMapper userEntityMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.matchSearchResultModelMapper = matchSearchResultModelMapper;
        this.watchManager = watchManager;
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.userEntityMapper = userEntityMapper;
    }

    public void init(String queryText) {
        this.queryText = queryText;
    }

    @Override protected void run() throws IOException, SQLException {
        List<MatchEntity> resultMatches = service.searchMatches(queryText);

        //Filter added matches
        // My following's watches
        List<Long> followingsAndMeIds =
          followManager.getUserFollowingIdsWithOwnUser(sessionRepository.getCurrentUserId());
        List<WatchEntity> watchesFromMyFollowing =
          watchManager.getWatchesNotEndedOrAdjurnedFromUsers(followingsAndMeIds);

        //Builder crappy stuff
        InfoListBuilder infoListBuilder = new InfoListBuilder(sessionRepository.getCurrentUser(), null, null,
          userEntityMapper);
        infoListBuilder.setWatches(watchesFromMyFollowing);
        Set<WatchEntity> validWatches = infoListBuilder.getValidWatches();

        List<Long> alreadyAddedMatchesIds = new ArrayList<>();
        if (validWatches!= null) {
            for (WatchEntity watch : validWatches) {
                alreadyAddedMatchesIds.add(watch.getIdMatch());
            }
        }

        // My team
        MatchEntity nextMatchFromMyTeam = service.getNextMatchWhereMyFavoriteTeamPlays(
          sessionRepository.getCurrentUser().getFavoriteTeamId());
        if (nextMatchFromMyTeam != null) {
            WatchEntity myTeamWatch =
              watchManager.getWatchByKeys(sessionRepository.getCurrentUserId(), nextMatchFromMyTeam.getIdMatch());
            if (myTeamWatch == null || myTeamWatch.getVisible()) {
                alreadyAddedMatchesIds.add(nextMatchFromMyTeam.getIdMatch());
            }
        }

        List<MatchSearchResultModel> matchModels = new ArrayList<>();
        for (MatchEntity match : resultMatches) {
            MatchSearchResultModel matchModel = matchSearchResultModelMapper.transform(match, alreadyAddedMatchesIds.contains(match.getIdMatch()));
            matchModels.add(matchModel);

        }
        postSuccessfulEvent(new SearchMatchResultEvent(matchModels));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
