package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class UserInfoResultEvent extends ShootrBaseJob.SuccessEvent<UserModel> {

    public UserInfoResultEvent(UserModel result) {
        super(result);
    }

}
