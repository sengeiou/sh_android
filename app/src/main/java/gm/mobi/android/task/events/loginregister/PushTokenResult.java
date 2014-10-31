package gm.mobi.android.task.events.loginregister;

import gm.mobi.android.task.jobs.ShootrBaseJob;

public class PushTokenResult extends ShootrBaseJob.SuccessEvent<String> {

    public PushTokenResult(String result) {
        super(result);
    }
}
