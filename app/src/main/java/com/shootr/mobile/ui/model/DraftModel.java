package com.shootr.mobile.ui.model;

import java.io.File;
import java.io.Serializable;

public class DraftModel implements Serializable {

    private Long idQueue;
    private BaseMessageModel shotModel;
    private File imageFile;

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public BaseMessageModel getBaseMessageModel() {
        return shotModel;
    }

    public void setShotModel(BaseMessageModel shotModel) {
        this.shotModel = shotModel;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
