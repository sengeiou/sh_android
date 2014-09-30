package gm.mobi.android.task.jobs.profile;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.profile.UserInfoResult;
import gm.mobi.android.task.jobs.CancellableJob;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends Job {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    @Inject SQLiteOpenHelper dbHelper;
    @Inject Bus bus;

    private int userId;
    private User currentUser;

    public GetUserInfoJob(Context context, int userId, User currentUser) {
        super(new Params(PRIORITY));
        this.userId = userId;
        this.currentUser = currentUser;

        GolesApplication.get(context).inject(this);
    }

    @Override public void onAdded() {
        /* noop */
    }

    @Override public void onRun() throws Throwable {
        User consultedUser = UserManager.getUserByIdUser(dbHelper.getReadableDatabase(), userId);
        if (consultedUser == null) {
            Timber.e("Retrieved null user from database with id %d", userId);
            //TODO control de errores, network, and stuff
            return;
        }

        // Get relationship
        int followRelationship =
            FollowManager.getFollowRelationship(dbHelper.getReadableDatabase(), currentUser, consultedUser);

        UserInfoResult result = new UserInfoResult(consultedUser, followRelationship);
        bus.post(result);
        //TODO control de errores
    }

    @Override protected void onCancel() {

    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}
