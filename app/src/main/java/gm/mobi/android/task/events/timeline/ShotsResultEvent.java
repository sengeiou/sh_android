package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.ResultEvent;
import java.util.List;

public class ShotsResultEvent extends ResultEvent<List<Shot>>{


    private List<Shot> shots;

    public ShotsResultEvent(int status){
        super(status);
    }

    @Override
    public ResultEvent setSuccessful(List<Shot> o) {
        this.setShots(o);
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

    public void setShots(List<Shot> shots){
        this.shots = shots;
    }
    public List<Shot> getShots(){
        return shots;
    }
}
