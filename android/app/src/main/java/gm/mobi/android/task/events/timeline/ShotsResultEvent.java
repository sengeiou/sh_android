package gm.mobi.android.task.events.timeline;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.ShotModel;
import java.util.List;

public class ShotsResultEvent extends BagdadBaseJob.SuccessEvent<List<ShotModel>> {

    public ShotsResultEvent(List<ShotModel> result) {
        super(result);
    }
}
