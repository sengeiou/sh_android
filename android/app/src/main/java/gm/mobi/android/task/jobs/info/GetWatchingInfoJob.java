package gm.mobi.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.data.SessionManager;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.MatchManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.manager.WatchManager;
import gm.mobi.android.db.objects.MatchEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.db.objects.WatchEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.info.WatchingInfoResult;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.UserWatchingModel;
import gm.mobi.android.ui.model.mappers.MatchModelMapper;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import gm.mobi.android.ui.model.mappers.UserWatchingModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

public class GetWatchingInfoJob extends BagdadBaseJob<WatchingInfoResult> {

    private static final int PRIORITY = 7;
    private BagdadService service;
    private SessionManager sessionManager;
    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;
    private UserManager userManager;
    private WatchManager watchManager;
    private MatchManager matchManager;
    private FollowManager followManager;

    List<Long> matchIds = new ArrayList<>();
    List<Long> userIds = new ArrayList<>();

    @Inject public GetWatchingInfoJob(Application application, Bus bus, NetworkUtil networkUtil, BagdadService service, SessionManager sessionManager, MatchModelMapper matchModelMapper, UserWatchingModelMapper userWatchingModelMapper, UserManager userManager, FollowManager followManager, SQLiteOpenHelper openHelper, WatchManager watchManager, MatchManager matchManager) {
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

    public void init() {

    }

    @Override protected void run() throws SQLException, IOException {
        MatchEntity nextMatchFromMyTeam = service.getNextMatchWhereMyFavoriteTeamPlays(getFavoriteTeamId());

        List<WatchEntity> watches =  getWatchesAndSaveThemInDataBase();
        List<MatchEntity> matches =  getMatchesAndSaveThem(matchIds);
        List<UserEntity> users = userManager.getUsersByIds(userIds);

        LongSparseArray<MatchEntity> matchesCatalog = new LongSparseArray<>(matches.size());
        for (MatchEntity match : matches) {
            matchesCatalog.put(match.getIdMatch(), match);
        }
        LongSparseArray<UserEntity> usersCatalog = new LongSparseArray<>(matches.size());
        for (UserEntity user : users) {
            usersCatalog.put(user.getIdUser(), user);
        }

        Map<MatchModel, List<UserWatchingModel>> matchesWithUsers = new HashMap<>();

        for (WatchEntity watch : watches) {
            Long idMatch = watch.getIdMatch();
            MatchModel match = matchModelMapper.toMatchModel(matchesCatalog.get(idMatch));
            if (matchesWithUsers.containsKey(match)) {
                matchesWithUsers.get(match).add(userWatchingModelMapper.toUserWatchingModel(usersCatalog.get(watch.getIdUser()),true));
            } else {
                List<UserWatchingModel> usersInMatch = new ArrayList<>();
                usersInMatch.add(userWatchingModelMapper.toUserWatchingModel(usersCatalog.get(watch.getIdUser()), true));
                MatchModel matchModel = matchModelMapper.toMatchModel(matchesCatalog.get(idMatch));
                matchesWithUsers.put(matchModel,usersInMatch);
            }
        }

        postSuccessfulEvent(new WatchingInfoResult(matchesWithUsers));
    }

    private List<MatchEntity> getMatchesAndSaveThem(List<Long> matchIds) throws IOException {
        List<MatchEntity> matches = service.getMatchesByIds(matchIds);
        matchManager.saveMatches(matches);

        return matches;
    }

    private List<WatchEntity> getWatchesAndSaveThemInDataBase() throws SQLException, IOException {
        List<WatchEntity> watches = service.getWatchesFromUsers(getIdsFromMyFollowingAndMe(), watchManager.getLastModifiedDate(
          GMContract.WatchTable.TABLE));
        watchManager.saveWatches(watches);
        for (WatchEntity watch : watches) {
            matchIds.add(watch.getIdMatch());
            userIds.add(watch.getIdUser());
        }
        return watches;
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
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public Long getFavoriteTeamId() {
        UserEntity currentUser = sessionManager.getCurrentUser();
        return currentUser.getFavoriteTeamId();
    }
}
