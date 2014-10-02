package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.SyncTableManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import timber.log.Timber;

public class GetUserInfoJob extends Job {

    private static final int PRIORITY = 3; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    @Inject SQLiteOpenHelper mDbHelper;
    @Inject Bus bus;
    @Inject BagdadService service;
    @Inject
    NetworkUtil mNetworkUtil;
    @Inject
    Application app;

    private Long userId;
    private User currentUser;

    public GetUserInfoJob(Context context, Long userId, User currentUser) {
        super(new Params(PRIORITY));
        this.userId = userId;
        this.currentUser = currentUser;
        GolesApplication.get(context).inject(this);
    }

    @Override public void onAdded() {
        /* noop */
    }


    public void retrieveDataFromDatabase(){
        Team favTeam = null;
        User consultedUser = UserManager.getUserByIdUser(mDbHelper.getReadableDatabase(), userId);
        if (consultedUser != null) {
            // Get relationship
            int followRelationship =FollowManager.getFollowRelationship(mDbHelper.getReadableDatabase(), currentUser,consultedUser);
            Long idTeamFav = consultedUser.getFavouriteTeamId();
            if(idTeamFav!=null) favTeam = TeamManager.getTeamByIdTeam(mDbHelper.getReadableDatabase(),idTeamFav);
            UserInfoResultEvent result = new UserInfoResultEvent(consultedUser, followRelationship, favTeam);
            bus.post(result);
            //TODO control de errores
        } else {
            Timber.i("User with id %d not found in local database. Retrieving from the service...",userId);
        }
    }

    public int getFollowRelationship(SQLiteDatabase db, User consultedUserFromService) throws IOException, SQLException {
        int resFollowRelationship;
        Follow getFollowingRelationshipBetweenMeAndUser = service.getFollowRelationship(consultedUserFromService.getIdUser(), currentUser.getIdUser(), UserDtoFactory.GET_FOLLOWING);
        Follow getFollowerRelationshipBetweenMeAndUser = service.getFollowRelationship(consultedUserFromService.getIdUser(), currentUser.getIdUser(), UserDtoFactory.GET_FOLLOWERS);
        FollowManager.saveFollow(db,getFollowerRelationshipBetweenMeAndUser);
        FollowManager.saveFollow(db,getFollowingRelationshipBetweenMeAndUser);
        resFollowRelationship = FollowManager.getFollowRelationship(db, currentUser,consultedUserFromService);
        db.close();
        return resFollowRelationship;
    }

    @Override public void onRun() throws Throwable {
        //We make this for a speed screen update
        retrieveDataFromDatabase();
        // Refresh anyways
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            User consultedUserFromService = service.getUserByIdUser(userId);
            int followRelationship = getFollowRelationship(db, consultedUserFromService);

            Team team = service.getTeamByIdTeam(consultedUserFromService.getFavouriteTeamId());
            //Store user and team in db
            UserManager.saveUser(db, consultedUserFromService);
            TeamManager.insertOrUpdateTeam(db,team);
            db.close();

            UserInfoResultEvent result = new UserInfoResultEvent(consultedUserFromService,followRelationship, team);
            bus.post(result);
        } catch (IOException e) {
            //TODO server error
            Timber.e("Error consulting user info from service. User id: %d", userId);
        }
    }

    @Override protected void onCancel() {

    }

    @Override protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }



    private boolean checkNetwork() {
        if (!mNetworkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        }
        return true;
    }
}
