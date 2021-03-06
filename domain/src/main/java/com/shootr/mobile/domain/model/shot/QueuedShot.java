package com.shootr.mobile.domain.model.shot;

import java.io.File;

public class QueuedShot {

    private Long idQueue;
    private BaseMessage shot;
    private boolean failed;
    private File imageFile;

    public QueuedShot() {
    }

    public QueuedShot(BaseMessage shot) {
        this.shot = shot;
    }

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public BaseMessage getBaseMessage() {
        return shot;
    }

    public void setBaseMessage(BaseMessage shot) {
        this.shot = shot;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueuedShot)) return false;

        QueuedShot that = (QueuedShot) o;

        if (idQueue != null ? !idQueue.equals(that.idQueue) : that.idQueue != null) return false;
        if (!shot.equals(that.shot)) return false;

        return true;
    }

    @Override public int hashCode() {
        int result = idQueue != null ? idQueue.hashCode() : 0;
        result = 31 * result + shot.hashCode();
        return result;
    }
}
