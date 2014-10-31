package gm.mobi.android.task.events.loginregister;

import gm.mobi.android.task.jobs.BagdadBaseJob;

public class PushTokenResult extends BagdadBaseJob.SuccessEvent<String> {

    public PushTokenResult(String result) {
        super(result);
    }
}
