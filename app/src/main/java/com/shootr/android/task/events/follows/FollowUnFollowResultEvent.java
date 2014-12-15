package com.shootr.android.task.events.follows;

import android.util.Pair;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class FollowUnFollowResultEvent extends ShootrBaseJob.SuccessEvent<Pair<Long, Boolean>> {

    public FollowUnFollowResultEvent(Long idUser, Boolean following) {
        super(new Pair<>(idUser, following));
    }

}
