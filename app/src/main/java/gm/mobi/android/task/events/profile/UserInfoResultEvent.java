package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class UserInfoResultEvent extends BagdadBaseJob.SuccessEvent<User> {

    private int relationship;

    public UserInfoResultEvent(User result, int relationship, String favouriteTeam) {
        super(result);
        this.relationship = relationship;
    }

    public int getRelationship() {
        return relationship;
    }

}
