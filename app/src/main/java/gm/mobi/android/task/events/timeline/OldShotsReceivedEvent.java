package gm.mobi.android.task.events.timeline;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.ShotVO;
import java.util.List;

public class OldShotsReceivedEvent extends BagdadBaseJob.SuccessEvent<List<ShotVO>> {

    public OldShotsReceivedEvent(List<ShotVO> result) {
        super(result);
    }
}
