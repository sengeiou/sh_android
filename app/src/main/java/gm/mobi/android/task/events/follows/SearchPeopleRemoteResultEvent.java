package gm.mobi.android.task.events.follows;

import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.ui.model.UserModel;
import java.util.List;

public class SearchPeopleRemoteResultEvent extends ShootrBaseJob.SuccessEvent<PaginatedResult<List<UserModel>>> {

    public SearchPeopleRemoteResultEvent(PaginatedResult<List<UserModel>> result) {
        super(result);
    }
}
