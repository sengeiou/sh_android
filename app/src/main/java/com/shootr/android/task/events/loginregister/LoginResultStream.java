package com.shootr.android.task.events.loginregister;


import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class LoginResultStream extends ShootrBaseJob.SuccessEvent<UserEntity>{

    public LoginResultStream(UserEntity result) {
        super(result);
    }
}
