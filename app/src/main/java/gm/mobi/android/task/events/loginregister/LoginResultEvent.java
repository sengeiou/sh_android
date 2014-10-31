package gm.mobi.android.task.events.loginregister;


import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.task.jobs.ShootrBaseJob;

public class LoginResultEvent extends ShootrBaseJob.SuccessEvent<UserEntity>{

    public LoginResultEvent(UserEntity result) {
        super(result);
    }
}
