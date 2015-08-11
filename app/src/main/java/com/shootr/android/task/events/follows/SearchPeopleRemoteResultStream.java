package com.shootr.android.task.events.follows;

import com.shootr.android.service.PaginatedResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import java.util.List;

public class SearchPeopleRemoteResultStream extends ShootrBaseJob.SuccessEvent<PaginatedResult<List<UserModel>>> {

    public SearchPeopleRemoteResultStream(PaginatedResult<List<UserModel>> result) {
        super(result);
    }
}
