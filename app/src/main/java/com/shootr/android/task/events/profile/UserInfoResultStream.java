package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class UserInfoResultStream extends ShootrBaseJob.SuccessEvent<UserModel> {

    public UserInfoResultStream(UserModel result) {
        super(result);
    }

}
