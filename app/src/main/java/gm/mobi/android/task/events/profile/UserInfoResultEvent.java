package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;

public class UserInfoResultEvent {
    private User user;
    private int relationship;
    private Team favouriteTeam;

    public UserInfoResultEvent(User user, int relationship, Team team) {
        this.user = user;
        this.relationship = relationship;
        this.favouriteTeam = team;
    }

    public User getUser() {
        return user;
    }

    public Team getFavouriteTeam(){
        return favouriteTeam;
    }

    public void setFavouriteTeam(Team team){
        this.favouriteTeam = team;
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
