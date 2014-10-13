package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.util.List;

public class SearchPeopleRemoteResultEvent extends BagdadBaseJob.SuccessEvent<PaginatedResult<List<User>>> {

    public SearchPeopleRemoteResultEvent(PaginatedResult<List<User>> result) {
        super(result);
    }
}
