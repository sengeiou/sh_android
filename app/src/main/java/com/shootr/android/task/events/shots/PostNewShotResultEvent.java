package com.shootr.android.task.events.shots;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class PostNewShotResultEvent extends ShootrBaseJob.SuccessEvent<ShotEntity> {

    public PostNewShotResultEvent(ShotEntity result) {
        super(result);
    }
}
