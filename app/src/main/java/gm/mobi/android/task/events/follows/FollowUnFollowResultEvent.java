package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;

public class FollowUnFollowResultEvent extends BagdadBaseJob.SuccessEvent<UserVO> {

int doIFollowHim;
public FollowUnFollowResultEvent(UserVO result) {
    super(result);
}

}
