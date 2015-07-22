package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class UploadProfilePhotoStream extends ShootrBaseJob.SuccessEvent<UserModel>{

    public UploadProfilePhotoStream(UserModel result) {
        super(result);
    }
}
