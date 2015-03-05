package com.shootr.android.ui.model;

public class DraftModel{

    private Long idQueue;
    private ShotModel shotModel;

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public ShotModel getShotModel() {
        return shotModel;
    }

    public void setShotModel(ShotModel shotModel) {
        this.shotModel = shotModel;
    }
}
