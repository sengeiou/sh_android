package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class FollowUnFollowResultEvent extends BagdadBaseJob.SuccessEvent<Follow> {

boolean doIFollowHim;
public FollowUnFollowResultEvent(Follow result,boolean doIFollowHim) {
    super(result);
    this.doIFollowHim = doIFollowHim;
  }

public boolean isDoIFollowHim(){
    return doIFollowHim;
}

}
