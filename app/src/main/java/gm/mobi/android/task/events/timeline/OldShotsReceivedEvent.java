package gm.mobi.android.task.events.timeline;

import java.util.List;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.loginregister.ResultEvent;
import gm.mobi.android.ui.adapters.TimelineAdapter;

public class OldShotsReceivedEvent extends ResultEvent{

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
    public ResultEvent setSuccessful(Object o) {
        return null;
    }

    @Override
    public ResultEvent setInvalid() {
        return null;
    }

    @Override
    public ResultEvent setServerError(Exception e) {
        return null;
    }
}
