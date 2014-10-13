package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.util.List;

public class NewShotsReceivedEvent extends BagdadBaseJob.SuccessEvent<List<Shot>> {

    private int newShotsCount;

    public NewShotsReceivedEvent(List<Shot> result, int newShotsCount) {
        super(result);
        this.newShotsCount = newShotsCount;
    }

    public int getNewShotsCount() {
        return newShotsCount;
    }

}
