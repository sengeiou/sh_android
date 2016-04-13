package com.shootr.mobile.domain;

import java.util.List;

public class ShotDetail {

    private Shot shot;
    private Shot parentShot;
    private List<Shot> replies;
    private List<Nicer> nicers;

    public List<Nicer> getNicers() {
        return nicers;
    }

    public void setNicers(List<Nicer> nicers) {
        this.nicers = nicers;
    }

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
