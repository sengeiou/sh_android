package com.shootr.android.task.events.follows;

import android.util.Pair;
import com.shootr.android.task.jobs.ShootrBaseJob;

public class FollowUnFollowResultStream extends ShootrBaseJob.SuccessEvent<Pair<String, Boolean>> {

    public FollowUnFollowResultStream(String idUser, Boolean following) {
        super(new Pair<>(idUser, following));
    }

}
