package gm.mobi.android.task.events.info;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.ui.model.UserWatchingModel;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WatchingInfoResult extends BagdadBaseJob.SuccessEvent<Map<MatchModel, Collection<UserWatchingModel>>> {

    public WatchingInfoResult(Map<MatchModel, Collection<UserWatchingModel>> result) {
        super(result);
    }
}
