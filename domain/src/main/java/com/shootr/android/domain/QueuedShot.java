package com.shootr.android.domain;

public class QueuedShot {

    private Long idQueue;
    private Shot shot;
    private boolean failed;

    public QueuedShot(Shot shot) {
        this.shot = shot;
    }

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public Shot getShot() {
        return shot;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueuedShot)) return false;

        QueuedShot that = (QueuedShot) o;

        if (idQueue != null ? !idQueue.equals(that.idQueue) : that.idQueue != null) return false;
        if (!shot.equals(that.shot)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idQueue != null ? idQueue.hashCode() : 0;
        result = 31 * result + shot.hashCode();
        return result;
    }
}
