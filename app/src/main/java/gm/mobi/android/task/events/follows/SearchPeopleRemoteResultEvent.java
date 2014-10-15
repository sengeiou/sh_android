package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;
import java.util.List;

public class SearchPeopleRemoteResultEvent extends BagdadBaseJob.SuccessEvent<PaginatedResult<List<UserVO>>> {

    public SearchPeopleRemoteResultEvent(PaginatedResult<List<UserVO>> result) {
        super(result);
    }
}
