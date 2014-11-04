package com.shootr.android.task.events.loginregister;

import com.shootr.android.task.jobs.ShootrBaseJob;

public class PushTokenResult extends ShootrBaseJob.SuccessEvent<String> {

    public PushTokenResult(String result) {
        super(result);
    }
}
