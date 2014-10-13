package gm.mobi.android.task.events.shots;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class PostNewShotResultEvent extends BagdadBaseJob.SuccessEvent<Shot> {

    public PostNewShotResultEvent(Shot result) {
        super(result);
    }
}
