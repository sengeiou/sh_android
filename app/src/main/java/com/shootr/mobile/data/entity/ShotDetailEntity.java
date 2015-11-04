package com.shootr.mobile.data.entity;

import java.util.List;

public class ShotDetailEntity {

    private ShotEntity shot;
    private ShotEntity parentShot;
    private List<ShotEntity> replies;

    public ShotEntity getShot() {
        return shot;
    }

    public void setShot(ShotEntity shot) {
        this.shot = shot;
    }

    public ShotEntity getParentShot() {
        return parentShot;
    }

    public void setParentShot(ShotEntity parentShot) {
        this.parentShot = parentShot;
    }

    public List<ShotEntity> getReplies() {
        return replies;
    }

    public void setReplies(List<ShotEntity> replies) {
        this.replies = replies;
    }
}
