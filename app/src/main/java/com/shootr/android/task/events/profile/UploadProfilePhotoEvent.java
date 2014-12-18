package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class UploadProfilePhotoEvent extends ShootrBaseJob.SuccessEvent<UserModel>{

    public UploadProfilePhotoEvent(UserModel result) {
        super(result);
    }
}
