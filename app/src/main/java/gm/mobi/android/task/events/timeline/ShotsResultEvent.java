package gm.mobi.android.task.events.timeline;

import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.loginregister.ResultEvent;

public class ShotsResultEvent extends ResultEvent{


    private List<Shot> shots;

    public ShotsResultEvent(int status){
        super(status);
    }



    @Override
    public ResultEvent successful(Object o) {
        this.setShots((List<Shot>)o);
        return null;
    }

    @Override
    public ResultEvent invalid() {
        return this;
    }

    @Override
    public ResultEvent serverError(Exception e) {
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
