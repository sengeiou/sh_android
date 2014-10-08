package gm.mobi.android.task.events.follows;

import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.events.ResultEvent;
import java.util.List;

public class SearchPeopleRemoteResultEvent extends ResultEvent<PaginatedResult<List<User>>> {

    private PaginatedResult<List<User>> result;

    public SearchPeopleRemoteResultEvent(int status) {
        super(status);
    }

    @Override public ResultEvent setSuccessful(PaginatedResult<List<User>> o) {
        result = o;
        return this;
    }

    @Override public ResultEvent setInvalid() {
        return this;
    }

    @Override public ResultEvent setServerError(Exception e) {
        this.setError(e);
        return this;
    }

    public PaginatedResult<List<User>> getResult() {
        return result;
    }
}
