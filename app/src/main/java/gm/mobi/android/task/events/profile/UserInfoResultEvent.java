package gm.mobi.android.task.events.profile;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;

public class UserInfoResultEvent extends BagdadBaseJob.SuccessEvent<UserModel> {

    private int doIFollowHim;

    public UserInfoResultEvent(UserModel result) {
        super(result);
    }

}
