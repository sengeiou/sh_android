package com.shootr.android.task.events.info;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.Collection;
import java.util.Map;

public class WatchingInfoResult extends ShootrBaseJob.SuccessEvent<Map<MatchModel, Collection<UserWatchingModel>>> {

    public WatchingInfoResult(Map<MatchModel, Collection<UserWatchingModel>> result) {
        super(result);
    }
}
