package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;

public class UserInfoResult {
    private User user;
    private int relationship;

    public UserInfoResult(User user, int relationship) {
        this.user = user;
        this.relationship = relationship;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }
}
