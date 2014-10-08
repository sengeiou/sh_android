package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends CancellableJob {

    private static final int PRIORITY = 3; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    Context context;

    SQLiteOpenHelper dbHelper;
    Bus bus;
    BagdadService service;

    NetworkUtil networkUtil;

    Application app;
    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;

    private Long userId;
    private User currentUser;
    SQLiteDatabase db;

    @Inject public GetUserInfoJob(Application context,Bus bus, SQLiteOpenHelper mDbHelper, BagdadService service, NetworkUtil mNetworkUtil,
                                  UserManager userManager, FollowManager followManager, TeamManager teamManager) {
        super(new Params(PRIORITY));
        this.context = context;
        this.bus = bus;
        this.dbHelper = mDbHelper;
        this.service = service;
        this.networkUtil = mNetworkUtil;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
    }

    public void init(Long userId, User currentUser){
        this.userId = userId;
        this.currentUser = currentUser;
    }

    @Override public void onAdded() {
        /* noop */
    }

    @Override
    protected void createDatabase(){
        db = createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers() {
        followManager.setDataBase(db);
        teamManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    public void retrieveDataFromDatabase(){
        Team favTeam = null;
        User consultedUser = userManager.getUserByIdUser(userId);
        if (consultedUser != null) {
            // Get relationship
            int followRelationship =followManager.getFollowRelationship( currentUser,consultedUser);
            Long idTeamFav = consultedUser.getFavouriteTeamId();
            if(idTeamFav!=null) favTeam = teamManager.getTeamByIdTeam(idTeamFav);
            UserInfoResultEvent result = new UserInfoResultEvent(consultedUser, followRelationship, favTeam);
            bus.post(result);
            //TODO control de errores
        } else {
            Timber.i("User with id %d not found in local database. Retrieving from the service...",userId);
        }
    }

    public int getFollowRelationship( User consultedUserFromService) throws IOException, SQLException {
        int resFollowRelationship;
        Follow getFollowingRelationshipBetweenMeAndUser = service.getFollowRelationship(consultedUserFromService.getIdUser(), currentUser.getIdUser(), UserDtoFactory.GET_FOLLOWING);
        Follow getFollowerRelationshipBetweenMeAndUser = service.getFollowRelationship(consultedUserFromService.getIdUser(), currentUser.getIdUser(), UserDtoFactory.GET_FOLLOWERS);

        followManager.saveFollow(getFollowerRelationshipBetweenMeAndUser);
        followManager.saveFollow(getFollowingRelationshipBetweenMeAndUser);
        resFollowRelationship = followManager.getFollowRelationship(currentUser,consultedUserFromService);
        return resFollowRelationship;
    }

    @Override public void run() throws  SQLException, IOException{
            //We make this for a speed screen update
            retrieveDataFromDatabase();
            // Refresh anyways
            User consultedUserFromService = service.getUserByIdUser(userId);
            int followRelationship = getFollowRelationship(consultedUserFromService);

            Team team = service.getTeamByIdTeam(consultedUserFromService.getFavouriteTeamId());
            //Store user and team in db
            userManager.saveUser(consultedUserFromService);
            if(team!=null)teamManager.insertOrUpdateTeam(team);
            UserInfoResultEvent result = new UserInfoResultEvent(consultedUserFromService,followRelationship, team);
            bus.post(result);

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
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        }
        return true;
    }
}
