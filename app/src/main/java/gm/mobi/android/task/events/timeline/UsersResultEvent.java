package gm.mobi.android.task.events.timeline;

import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.loginregister.ResultEvent;

public class UsersResultEvent extends ResultEvent {

    protected List<User> users;
    public List<Integer> followingUserIds;
    public UsersResultEvent(int status){
        super(status);
    }

    @Override
    public ResultEvent setSuccessful(Object o) {
        this.setUsers((List<User>)o);
        return this;
    }

    @Override
    public ResultEvent setInvalid() {
        return this;
    }

    @Override
    public ResultEvent setServerError(Exception e) {
        this.setError(e);
        return this;
    }

    public void setUsers(List<User> users){
        followingUserIds = new ArrayList<>();
        this.users = users;

        for(User u:users){
            followingUserIds.add(u.getIdUser());
        }
    }

    public List<Integer> getFollowingUserIds(){
        return followingUserIds;
    }

    public List<User> getUsers(){
        return users;
    }
}
