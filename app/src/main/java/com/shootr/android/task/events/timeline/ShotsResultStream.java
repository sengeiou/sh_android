package com.shootr.android.task.events.timeline;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public class ShotsResultStream extends ShootrBaseJob.SuccessEvent<List<ShotModel>> {

    public ShotsResultStream(List<ShotModel> result) {
        super(result);
    }
}
