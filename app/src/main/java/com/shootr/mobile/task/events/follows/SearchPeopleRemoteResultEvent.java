package com.shootr.mobile.task.events.follows;

import com.shootr.mobile.task.jobs.ShootrBaseJob;
import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public class SearchPeopleRemoteResultEvent extends ShootrBaseJob.SuccessEvent<List<UserModel>> {

    public SearchPeopleRemoteResultEvent(List<UserModel> result) {
        super(result);
    }
}
