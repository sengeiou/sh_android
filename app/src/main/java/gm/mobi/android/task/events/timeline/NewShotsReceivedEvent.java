package gm.mobi.android.task.events.timeline;

import java.util.List;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.ResultEvent;

public class NewShotsReceivedEvent extends ResultEvent<List<Shot>>{

    private List<Shot> shots;

    public NewShotsReceivedEvent(int status) {
        super(status);
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> newerShots){
        this.shots = newerShots;
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
