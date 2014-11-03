package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.ui.model.UserModel;

public class FollowUnFollowResultEvent extends ShootrBaseJob.SuccessEvent<UserModel> {

public FollowUnFollowResultEvent(UserModel result) {
    super(result);
}

}
