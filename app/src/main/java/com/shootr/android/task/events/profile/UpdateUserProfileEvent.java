package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;

public class UpdateUserProfileEvent extends ShootrBaseJob.SuccessEvent<Void> {

    public UpdateUserProfileEvent(Void result) {
        super(result);
    }
}
