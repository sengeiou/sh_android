package gm.mobi.android.task.events.timeline;

import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.task.events.ResultEvent;
import java.util.List;

public class NewShotsReceivedEvent extends ResultEvent<List<Shot>>{

    private List<Shot> allShots;

    public int getNewShotsCount() {
        return newShotsCount;
    }

    public void setNewShotsCount(int newShotsCount) {
        this.newShotsCount = newShotsCount;
    }

    private int newShotsCount;

    public NewShotsReceivedEvent(int status) {
        super(status);
    }

    public List<Shot> getAllShots() {
        return allShots;
    }

    public void setAllShots(List<Shot> newerShots){
        this.allShots = newerShots;
    }

    @Override
    public ResultEvent setSuccessful(List<Shot> o) {
        allShots = o;
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
