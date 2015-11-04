package com.shootr.mobile.task.events.follows;

import com.shootr.mobile.task.jobs.ShootrBaseJob;
import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public class FollowsResultEvent extends ShootrBaseJob.SuccessEvent<List<UserModel>> {

    public FollowsResultEvent(List<UserModel> result) {
        super(result);
    }

}
