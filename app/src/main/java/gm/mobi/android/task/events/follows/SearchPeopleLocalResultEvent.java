package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.util.List;

public class SearchPeopleLocalResultEvent extends BagdadBaseJob.SuccessEvent<List<User>> {

    public SearchPeopleLocalResultEvent(List<User> result) {
        super(result);
    }
}
