package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.util.List;

public class FollowsResultEvent extends BagdadBaseJob.SuccessEvent<List<User>> {

    public FollowsResultEvent(List<User> result) {
        super(result);
    }


}
