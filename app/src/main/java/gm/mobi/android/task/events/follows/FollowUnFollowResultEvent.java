package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;

public class FollowUnFollowResultEvent extends BagdadBaseJob.SuccessEvent<UserModel> {

public FollowUnFollowResultEvent(UserModel result) {
    super(result);
}

}
