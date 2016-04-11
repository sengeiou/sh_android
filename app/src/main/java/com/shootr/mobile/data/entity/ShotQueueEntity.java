package com.shootr.mobile.data.entity;

public class ShotQueueEntity extends ShotEntity {

    private Long idQueue;
    private Integer failed;
    private String imageFile;

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
