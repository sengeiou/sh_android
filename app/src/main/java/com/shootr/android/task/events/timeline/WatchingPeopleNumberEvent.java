package com.shootr.android.task.events.timeline;

import com.shootr.android.task.jobs.ShootrBaseJob;

public class WatchingPeopleNumberEvent extends ShootrBaseJob.SuccessEvent<Integer> {

    public WatchingPeopleNumberEvent(Integer result) {
        super(result);
    }
}
