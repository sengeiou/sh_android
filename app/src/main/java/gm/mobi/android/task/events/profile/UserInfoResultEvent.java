package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class UserInfoResultEvent extends BagdadBaseJob.SuccessEvent<User> {

    private int relationship;
    private String favouriteTeam;

    public UserInfoResultEvent(User result, int relationship, String favouriteTeam) {
        super(result);
        this.relationship = relationship;
        this.favouriteTeam = favouriteTeam;
    }

    public int getRelationship() {
        return relationship;
    }

    public String getFavouriteTeam() {
        return favouriteTeam;
    }
}
