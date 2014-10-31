package gm.mobi.android.task.events.loginregister;


import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class LoginResultEvent extends BagdadBaseJob.SuccessEvent<UserEntity>{

    public LoginResultEvent(UserEntity result) {
        super(result);
    }
}
