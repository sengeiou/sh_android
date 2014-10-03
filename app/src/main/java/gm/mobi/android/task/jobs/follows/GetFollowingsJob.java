package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.SyncTableManager;
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
import gm.mobi.android.task.jobs.CancellableJob;
import timber.log.Timber;

public class GetFollowingsJob extends CancellableJob{

    private static final int PRIORITY = 6; //TODO Define next values for our queue
    private static final int RETRY_ATTEMPTS = 3;

    @Inject Application app;
    @Inject NetworkUtil networkUtil;
    @Inject Bus bus;
    @Inject SQLiteOpenHelper mDbHelper;
    @Inject BagdadService service;
    @Inject UserManager userManager;
    private User currentUser;

    public GetFollowingsJob(Context context, User user){
        super(new Params(PRIORITY));
        this.currentUser = user;
        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onAdded() {
        /* no-op */
    }


    private List<Follow> getFollowingsIdsFromServer(){
        List<Follow> followingsIds = new ArrayList<>();
        Long modifiedFollows = SyncTableManager.getLastModifiedDate(mDbHelper.getReadableDatabase(), GMContract.FollowTable.TABLE);
        try{
            followingsIds = service.getFollows(currentUser.getIdUser(), modifiedFollows, UserDtoFactory.GET_FOLLOWING, true);

        }catch(ServerException e){
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

    private List<User> getUsersByFollowingIdsFromServer(List<Long> followingsIds){
        List<User> users = null;
        try{
            users = service.getUsersByUserIdList(followingsIds);
        }catch(ServerException e){
            if (e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError();
            } else {
                sendServerError(e);
            }
        } catch (IOException e) {
            sendServerError(e);
        }
        return users;
    }

    @Override
    public void onRun() throws Throwable {
        if (isCancelled()) return;
        if (!checkConnection()) return;

            // 1. Download followings ids
            List<Follow> followings = getFollowingsIdsFromServer();
            if (followings == null) {
                sendServerError(null);
                Timber.e("Unknown error downloading followings list");
                return;
            }
            Timber.d("Downloaded %d following relations", followings.size());

            List<Long> followingIds = new ArrayList<>(followings.size());
            for (Follow following : followings) {
                followingIds.add(following.getFollowedUser());
            }

            if (followings.size() == 0) {
                sendSucces(null);
                return;
            }
            // 2. Download users from those followings
            List<User> usersFollowing = getUsersByFollowingIdsFromServer(followingIds);
            if (usersFollowing == null) {
                sendServerError(null);
                Timber.e("Unknown error downloading followings' users");
                return;
            }
            List<Team> teams ;
            Set<Long> idTeams = new HashSet<>();
            for (User user : usersFollowing) {
                if (user.getFavouriteTeamId() != null) {
                    idTeams.add(user.getFavouriteTeamId());
                }
            }
            teams = getTeamsByTeamIds(idTeams);
            Timber.d("Downloaded %d followings' users", usersFollowing.size());
            Timber.d("Downloaded %d teams' users", teams.size());

            if (isCancelled()) return;

            // Save and send result
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            FollowManager.saveFollows(db, followings);
            userManager.saveUsers(usersFollowing);
            TeamManager.saveTeams(db, teams);
            db.close();
            sendSucces(usersFollowing);

    }

    private List<Team> getTeamsByTeamIds(Set<Long> teamIds){
        List<Team> resTeams = null;
        Long modifiedTeams = SyncTableManager.getLastModifiedDate(mDbHelper.getReadableDatabase(), GMContract.TeamTable.TABLE);
        try{
            resTeams= service.getTeamsByIdTeams(teamIds, modifiedTeams);
        }catch(IOException e){
            sendServerError(e);
        }
        return  resTeams;
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
