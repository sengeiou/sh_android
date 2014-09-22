package gm.mobi.android.task.events.timeline;

import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.task.events.loginregister.ResultEvent;

public class FollowsResultEvent extends ResultEvent{

    private List<Follow> follows;
    private List<Integer> followingIds;
    public FollowsResultEvent(int status) {
        super(status);
    }

    @Override
    public ResultEvent successful(Object o) {
        this.setFollows((List<Follow>)o);
        return this;
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

    public void setFollows(List<Follow> follows){
        this.follows = follows;
        followingIds = new ArrayList<>();
        for(Follow f: follows){
            followingIds.add(f.getFollowedUser());
        }

    }
    public List<Integer> getFollowingIds(){
        return followingIds;
    }
    public List<Follow> getFollows(){
        return follows;
    }
}
