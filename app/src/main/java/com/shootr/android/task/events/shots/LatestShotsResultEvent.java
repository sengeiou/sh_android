package com.shootr.android.task.events.shots;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public class LatestShotsResultEvent extends ShootrBaseJob.SuccessEvent<List<ShotModel>>  {

    public LatestShotsResultEvent(List<ShotModel> result) {
        super(result);
    }
}
