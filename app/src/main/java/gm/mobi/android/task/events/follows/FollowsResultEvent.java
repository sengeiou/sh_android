package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ResultEvent;
import java.util.List;

public class FollowsResultEvent extends ResultEvent<List<User>> {


    private List<User> follows;

    public FollowsResultEvent(int status) {
        super(status);
    }

    @Override
    public ResultEvent setSuccessful(List<User> o) {
        follows = o;
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

    public List<User> getFollows() {
        return follows;
    }

}
