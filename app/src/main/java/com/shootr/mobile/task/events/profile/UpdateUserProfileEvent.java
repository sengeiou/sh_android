package com.shootr.mobile.task.events.profile;

import com.shootr.mobile.task.jobs.ShootrBaseJob;

public class UpdateUserProfileEvent extends ShootrBaseJob.SuccessEvent<Void> {

    public UpdateUserProfileEvent(Void result) {
        super(result);
    }
}
