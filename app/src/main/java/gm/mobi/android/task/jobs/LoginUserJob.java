package gm.mobi.android.task.jobs;


import android.content.Context;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;

import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.LoginResultEvent;
import gm.mobi.android.util.NetworkUtils;

public class LoginUserJob extends CancellableJob {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMTS = 3;
    private String usernameEmail;
    private String password;
    private OpenHelper mDbHelper;

    public LoginUserJob(Context context, String usernameEmail, String password) {
        super(new Params(PRIORITY)
                .delayInMs(1000)//debug
        );
        this.usernameEmail = usernameEmail;
        this.password = password;
        this.mDbHelper = new OpenHelper(context);
    }


    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    public void onRun() throws Throwable {
        //TODO enviar información de login al servidor y comunicar el resultado
        if(isCancelled()) return;
        // TODO network available? (ConnectionNotAvailableEvent)
//        BusProvider.getInstance().post(new ConnectionNotAvailableEvent());

        // Mock login:
        if (usernameEmail.equals("rafa.vazsan@gmail.com") || usernameEmail.equals("sloydev")) {
            User mockUser = new User();
            mockUser.setId(1);
            mockUser.setName("Rafa");
            mockUser.setSessionToken("Nnananananananana");
            mockUser.setEmail(usernameEmail);
            mockUser.setUserName("rafavazsan");
            mockUser.setFavouriteTeamId(1);
            mockUser.setPhoto("http://example.com");
            UserManager.saveUser(mDbHelper.getWritableDatabase(), mockUser);

            BusProvider.getInstance().post(LoginResultEvent.successful(mockUser));
        } else {
            BusProvider.getInstance().post(LoginResultEvent.invalid());
        }

    }

    @Override
    protected void onCancel() {
        /* no-op */
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMTS;
    }
}
