package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.util.List;

public class ShotsResultEvent extends BagdadBaseJob.SuccessEvent<List<Shot>> {

    public ShotsResultEvent(List<Shot> result) {
        super(result);
    }
}
