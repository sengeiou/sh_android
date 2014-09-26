package gm.mobi.android.task.events.shots;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.ResultEvent;

public class PostNewShotResultEvent extends ResultEvent<Shot>{


    private Shot shot;

    public PostNewShotResultEvent(int status) {
        super(status);
    }

    @Override
    public ResultEvent setSuccessful(Shot o) {
        shot = o;
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
