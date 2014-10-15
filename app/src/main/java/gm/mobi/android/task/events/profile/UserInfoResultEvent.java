package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class UserInfoResultEvent extends BagdadBaseJob.SuccessEvent<User> {

    private int doIFollowHim;

    public UserInfoResultEvent(User result, int doIFollowHim) {
        super(result);
        this.doIFollowHim = doIFollowHim;
    }

    public int doIFollowHim(){
        return doIFollowHim;
    }

}
