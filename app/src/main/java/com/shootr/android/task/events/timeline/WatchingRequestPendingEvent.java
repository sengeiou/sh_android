package com.shootr.android.task.events.timeline;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.WatchingRequestModel;
import java.util.List;

public class WatchingRequestPendingEvent extends ShootrBaseJob.SuccessEvent<List<WatchingRequestModel>> {

    public WatchingRequestPendingEvent(List<WatchingRequestModel> result) {
        super(result);
    }
}
