package gm.mobi.android.task.events.timeline;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.ShotModel;
import java.util.List;

public class NewShotsReceivedEvent extends BagdadBaseJob.SuccessEvent<List<ShotModel>> {

    private int newShotsCount;

    public NewShotsReceivedEvent(List<ShotModel> result, int newShotsCount) {
        super(result);
        this.newShotsCount = newShotsCount;
    }

    public int getNewShotsCount() {
        return newShotsCount;
    }

}
