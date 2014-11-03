package com.shootr.android.task.events.loginregister;


import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class LoginResultEvent extends ShootrBaseJob.SuccessEvent<UserEntity>{

    public LoginResultEvent(UserEntity result) {
        super(result);
    }
}
