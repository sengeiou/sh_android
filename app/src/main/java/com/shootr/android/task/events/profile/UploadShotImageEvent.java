package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class UploadShotImageEvent extends ShootrBaseJob.SuccessEvent<String>{

    public UploadShotImageEvent(String result) {
        super(result);
    }
}
