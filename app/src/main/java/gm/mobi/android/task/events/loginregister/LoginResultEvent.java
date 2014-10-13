package gm.mobi.android.task.events.loginregister;


import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class LoginResultEvent extends BagdadBaseJob.SuccessEvent<User>{

    public LoginResultEvent(User result) {
        super(result);
    }
}
