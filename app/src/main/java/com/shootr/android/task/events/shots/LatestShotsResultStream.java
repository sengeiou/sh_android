package com.shootr.android.task.events.shots;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public class LatestShotsResultStream extends ShootrBaseJob.SuccessEvent<List<ShotModel>>  {

    public LatestShotsResultStream(List<ShotModel> result) {
        super(result);
    }
}
