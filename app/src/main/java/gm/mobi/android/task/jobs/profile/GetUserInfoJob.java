package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends Job {

    private static final int PRIORITY = 3; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    @Inject SQLiteOpenHelper dbHelper;
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

    @Override public void onRun() throws Throwable {
        Team favTeam = null;
        User consultedUser = UserManager.getUserByIdUser(dbHelper.getReadableDatabase(), userId);
        if (consultedUser != null) {
            // Get relationship
            int followRelationship =
                FollowManager.getFollowRelationship(dbHelper.getReadableDatabase(), currentUser,
                    consultedUser);
            Long idTeamFav = consultedUser.getFavouriteTeamId();
            if(idTeamFav!=null) favTeam = TeamManager.getTeamByIdTeam(dbHelper.getReadableDatabase(),idTeamFav);
            UserInfoResultEvent result = new UserInfoResultEvent(consultedUser, followRelationship, favTeam);
            bus.post(result);
            //TODO control de errores
        } else {
            Timber.i("User with id %d not found in local database. Retrieving from the service...",
                userId);
        }

        // Refresh anyways
        try {
            User consultedUserFromService = service.getUserByIdUser(userId);
            //TODO retrieve follows relations also
            //TODO retrieve team
            //Store user in db
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            UserManager.saveUser(db, consultedUserFromService);
            Long idTeamFav = consultedUser.getFavouriteTeamId();
            if(idTeamFav!=null) favTeam = TeamManager.getTeamByIdTeam(dbHelper.getReadableDatabase(),idTeamFav);
            db.close();

            UserInfoResultEvent result = new UserInfoResultEvent(consultedUserFromService,0, favTeam);
   //TODO meter follow obtenido y calculado
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
