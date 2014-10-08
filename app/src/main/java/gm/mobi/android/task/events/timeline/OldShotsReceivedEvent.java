package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.ResultEvent;
import java.util.List;

public class OldShotsReceivedEvent extends ResultEvent<List<Shot>>{

    private List<Shot> shots;

    public OldShotsReceivedEvent(int status) {
        super(status);
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots){
        this.shots = shots;
    }

    @Override
    public ResultEvent setSuccessful(List<Shot> o) {
        shots = o;
        return this;
    }

    @Override
    public ResultEvent setInvalid() {
        return this;
    }

    @Override
    public ResultEvent setServerError(Exception e) {
        return this;
    }
}
