package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;

public class UserInfoResultEvent extends BagdadBaseJob.SuccessEvent<UserVO> {

    private int doIFollowHim;

    public UserInfoResultEvent(UserVO result) {
        super(result);
    }

}
