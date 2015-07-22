package com.shootr.android.task.events.shots;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class PostNewShotResultStream extends ShootrBaseJob.SuccessEvent<ShotEntity> {

    public PostNewShotResultStream(ShotEntity result) {
        super(result);
    }
}
