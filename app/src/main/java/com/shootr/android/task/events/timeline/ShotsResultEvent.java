package gm.mobi.android.task.events.timeline;

import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.ui.model.ShotModel;
import java.util.List;

public class ShotsResultEvent extends ShootrBaseJob.SuccessEvent<List<ShotModel>> {

    public ShotsResultEvent(List<ShotModel> result) {
        super(result);
    }
}
