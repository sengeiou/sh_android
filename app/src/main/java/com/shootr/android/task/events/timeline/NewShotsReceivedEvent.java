package com.shootr.android.task.events.timeline;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public class NewShotsReceivedEvent extends ShootrBaseJob.SuccessEvent<List<ShotModel>> {

    private int newShotsCount;

    public NewShotsReceivedEvent(List<ShotModel> result, int newShotsCount) {
        super(result);
        this.newShotsCount = newShotsCount;
    }

    public int getNewShotsCount() {
        return newShotsCount;
    }

}
