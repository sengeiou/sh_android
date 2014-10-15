package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;
import java.util.List;

public class FollowsResultEvent extends BagdadBaseJob.SuccessEvent<List<UserVO>> {

    public FollowsResultEvent(List<UserVO> result) {
        super(result);
    }


}
