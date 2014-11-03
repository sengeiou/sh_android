package gm.mobi.android.task.events.shots;

import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.task.jobs.ShootrBaseJob;

public class PostNewShotResultEvent extends ShootrBaseJob.SuccessEvent<ShotEntity> {

    public PostNewShotResultEvent(ShotEntity result) {
        super(result);
    }
}
