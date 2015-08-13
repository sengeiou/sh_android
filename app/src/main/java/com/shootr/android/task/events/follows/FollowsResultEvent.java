package com.shootr.android.task.events.follows;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import java.util.List;

public class FollowsResultEvent extends ShootrBaseJob.SuccessEvent<List<UserModel>> {

    public FollowsResultEvent(List<UserModel> result) {
        super(result);
    }

}
