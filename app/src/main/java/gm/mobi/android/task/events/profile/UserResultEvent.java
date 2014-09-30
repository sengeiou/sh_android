package gm.mobi.android.task.events.profile;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ResultEvent;

public class UserResultEvent extends ResultEvent<User>{

    private User user;

    public UserResultEvent(int status){
        super(status);
    }

    @Override
    public ResultEvent setSuccessful(User o) {
        user = o;
        return this;
    }

    @Override
    public ResultEvent setInvalid() {
        return this;
    }

    @Override
    public ResultEvent setServerError(Exception e) {
        setError(e);
        return this;
    }
}
