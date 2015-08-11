package com.shootr.android.domain;

import java.util.List;

public class ShotDetail {

    private Shot shot;
    private Shot parentShot;
    private List<Shot> replies;

    public Shot getShot() {
        return shot;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

    public Shot getParentShot() {
        return parentShot;
    }

    public void setParentShot(Shot parentShot) {
        this.parentShot = parentShot;
    }

    public List<Shot> getReplies() {
        return replies;
    }

    public void setReplies(List<Shot> replies) {
        this.replies = replies;
    }

}
