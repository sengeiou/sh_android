package gm.mobi.android.task.events.shots;

import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class PostNewShotResultEvent extends BagdadBaseJob.SuccessEvent<ShotEntity> {

    public PostNewShotResultEvent(ShotEntity result) {
        super(result);
    }
}
