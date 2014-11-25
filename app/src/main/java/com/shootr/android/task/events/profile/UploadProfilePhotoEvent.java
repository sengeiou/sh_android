package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;

public class UploadProfilePhotoEvent extends ShootrBaseJob.SuccessEvent<String>{

    public UploadProfilePhotoEvent(String result) {
        super(result);
    }
}
