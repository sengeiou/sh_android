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
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.task.jobs.BagdadBaseJob;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

public class GetFollowingsJob extends BagdadBaseJob<List<User>> {

    private static final int PRIORITY = 6; //TODO Define next values for our queue
    private static final int RETRY_ATTEMPTS = 3;

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;

    private User currentUser;

    @Inject
    public GetFollowingsJob(Application application, NetworkUtil networkUtil, Bus bus, BagdadService service, UserManager userManager, FollowManager followManager, TeamManager teamManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
    }

    public void init(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected void run() throws SQLException, IOException {
        // 1. Download followings ids
        List<User> followings = getFollowingsFromServer();
        List<Follow> follows = getFollowsFromServer();
        if (followings == null) {
            //TODO send some error?
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
        Timber.d("Downloaded %d teams' users", teams != null ? teams.size() : null);

        // Save and send result
        userManager.saveUsers(followings);
        if (teams != null) teamManager.saveTeams(teams);
        followManager.saveFollows(follows);
        postSuccessfulEvent(followings);

    }

    private List<Follow> getFollowsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        return service.getFollows(currentUser.getIdUser(), modifiedFollows, UserDtoFactory.GET_FOLLOWING, true);
    }

    private List<User> getFollowingsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        List<User> following;
        following = service.getFollowings(currentUser.getIdUser(), modifiedFollows);
        return following;
    }

    private List<Team> getTeamsByTeamIds(Set<Long> teamIds) throws IOException {
        List<Team> resTeams = new ArrayList<>();
        Long modifiedTeams = teamManager.getLastModifiedDate(GMContract.TeamTable.TABLE);
        resTeams = service.getTeamsByIdTeams(teamIds, modifiedTeams);
        return resTeams;
    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
        teamManager.setDataBase(db);

    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

}
