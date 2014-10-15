package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class FollowUnFollowResultEvent extends BagdadBaseJob.SuccessEvent<Follow> {

int doIFollowHim;
public FollowUnFollowResultEvent(Follow result,int doIFollowHim) {
    super(result);
    this.doIFollowHim = doIFollowHim;
  }

public int isDoIFollowHim(){
    return doIFollowHim;
}

}
