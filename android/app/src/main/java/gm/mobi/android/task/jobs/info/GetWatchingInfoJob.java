package gm.mobi.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LongSparseArray;
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
import gm.mobi.android.ui.model.UserWatchingModel;
import gm.mobi.android.ui.model.mappers.MatchModelMapper;
import gm.mobi.android.ui.model.mappers.UserWatchingModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

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

    @Inject public GetWatchingInfoJob(Application application, Bus bus, NetworkUtil networkUtil, BagdadService service,
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

    public void init() {

    }

    @Override protected void run() throws SQLException, IOException {
        MatchEntity nextMatchFromMyTeam = service.getNextMatchWhereMyFavoriteTeamPlays(getFavoriteTeamId());
        //TODO hacer algo con este tío...

        List<WatchEntity> watches = getWatches();

        InfoListBuilder.DataProvider infoBuilderDataProvider = new InfoListBuilder.DataProvider() {
            @Override public List<UserEntity> getUsersByIds(List<Long> userIds) {
                return userManager.getUsersByIds(userIds);
            }

            @Override public List<MatchEntity> getMatchesByIds(List<Long> matchIds) {
                try {
                    return service.getMatchesByIds(matchIds);
                } catch (IOException e) {
                    Timber.e(e, "FATAL ERROR: Server failed while getting matches, and the exception wasn't handled :'(");
                    return null;
                }
            }
        };

        InfoListBuilder infoListBuilder = new InfoListBuilder(infoBuilderDataProvider, sessionManager.getCurrentUserId(), matchModelMapper, userWatchingModelMapper);

        Map<MatchModel, Collection<UserWatchingModel>> resultMap = infoListBuilder.build(watches);
        postSuccessfulEvent(new WatchingInfoResult(resultMap));
    }

    private List<MatchEntity> getMatchesAndSaveThem(List<Long> matchIds) throws IOException {
        List<MatchEntity> matches = service.getMatchesByIds(matchIds);
        matchManager.saveMatches(matches);

        return matches;
    }

    private List<WatchEntity> getWatches() throws SQLException, IOException {
        Long watchLastModifiedDate = watchManager.getLastModifiedDate(GMContract.WatchTable.TABLE);
        watchLastModifiedDate = 0L; //TODO retrieve modified watches only
        List<WatchEntity> watches = service.getWatchesFromUsers(getIdsFromMyFollowingAndMe(), watchLastModifiedDate);
        //TODO save in database
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
