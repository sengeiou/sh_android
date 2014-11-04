package com.shootr.android.task.events.follows;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;

public class FollowUnFollowResultEvent extends ShootrBaseJob.SuccessEvent<UserModel> {

public FollowUnFollowResultEvent(UserModel result) {
    super(result);
}

}
