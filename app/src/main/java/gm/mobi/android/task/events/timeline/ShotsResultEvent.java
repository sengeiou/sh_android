package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.ShotVO;
import java.util.List;

public class ShotsResultEvent extends BagdadBaseJob.SuccessEvent<List<ShotVO>> {

    public ShotsResultEvent(List<ShotVO> result) {
        super(result);
    }
}
