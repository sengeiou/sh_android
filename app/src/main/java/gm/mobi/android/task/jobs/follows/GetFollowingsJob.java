package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowingsJob extends BagdadBaseJob {

    private static final int PRIORITY = 6; //TODO Define next values for our queue
    private static final int RETRY_ATTEMPTS = 3;

    Application app;
    NetworkUtil networkUtil;
    Bus bus;
    BagdadService service;
    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;
    private User currentUser;
    SQLiteDatabase db;

    @Inject
    public GetFollowingsJob(Application context, NetworkUtil networkUtil, Bus bus, BagdadService service, UserManager userManager, FollowManager followManager, TeamManager teamManager) {
        super(new Params(PRIORITY));
        this.app = context;
        this.networkUtil = networkUtil;
        this.bus = bus;
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
    }

    public void init(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    private List<Follow> getFollowsFromServer() {
        List<Follow> followingsIds = new ArrayList<>();

        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        try {
            followingsIds = service.getFollows(currentUser.getIdUser(), modifiedFollows, UserDtoFactory.GET_FOLLOWING, true);

        } catch (ServerException e) {
            if (e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError();
            } else {
                sendServerError(e);
            }
        } catch (IOException e) {
            sendServerError(e);
        }
        return followingsIds;
    }


    private List<User> getFollowingsFromServer() {
        List<User> following = new ArrayList<>();

        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        try {
            following = service.getFollowings(currentUser.getIdUser(), modifiedFollows);

        } catch (ServerException e) {
            if (e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError();
            } else {
                sendServerError(e);
            }
        } catch (IOException e) {
            sendServerError(e);
        }
        return following;
    }

    @Override
    protected void createDatabase() {
        db = createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers() {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
        teamManager.setDataBase(db);

    }

    @Override
    protected void run() throws SQLException {
        if (isCancelled()) return;
        if (!checkConnection()) return;

        // 1. Download followings ids
        List<User> followings = getFollowingsFromServer();
        List<Follow> follows = getFollowsFromServer();
        if (followings == null) {
            sendServerError(null);
            Timber.e("Unknown error downloading followings list");
            return;
        }
        Timber.d("Downloaded %d following relations", followings.size());
        // 2. Download users from those followings
        List<Team> teams;
        Set<Long> idTeams = new HashSet<>();
        for (User user : followings) {
            if (user.getFavouriteTeamId() != null) {
                idTeams.add(user.getFavouriteTeamId());
            }
        }
        teams = getTeamsByTeamIds(idTeams);
        Timber.d("Downloaded %d followings' users", followings.size());
        Timber.d("Downloaded %d teams' users", teams!=null ? teams.size() : null);

        if (isCancelled()) return;

        // Save and send result
        userManager.saveUsers(followings);
        if(teams!=null) teamManager.saveTeams(teams);
        followManager.saveFollows(follows);
        sendSucces(followings);

    }

    private List<Team> getTeamsByTeamIds(Set<Long> teamIds) {
        List<Team> resTeams = new ArrayList<>();
        Long modifiedTeams = teamManager.getLastModifiedDate(GMContract.TeamTable.TABLE);
        try {
            resTeams = service.getTeamsByIdTeams(teamIds, modifiedTeams);
        } catch (IOException e) {
            sendServerError(e);
        }
        return resTeams;
    }

    private boolean checkConnection() {
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onCancel() {
        /* no-op */
    }

    private void sendSucces(List<User> usersFollowing) {
        FollowsResultEvent result = new FollowsResultEvent(FollowsResultEvent.STATUS_SUCCESS);
        result.setSuccessful(usersFollowing);
        bus.post(result);
    }

    private void sendCredentialError() {
        FollowsResultEvent fResultEvent = new FollowsResultEvent(FollowsResultEvent.STATUS_INVALID);
        bus.post(fResultEvent.setInvalid());
    }

    private void sendServerError(Exception e) {
        FollowsResultEvent fResultEvent = new FollowsResultEvent(FollowsResultEvent.STATUS_SERVER_FAILURE);
        bus.post(fResultEvent.setServerError(e));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }
}
