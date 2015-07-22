package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;

public class UpdateUserProfileStream extends ShootrBaseJob.SuccessEvent<Void> {

    public UpdateUserProfileStream(Void result) {
        super(result);
    }
}
