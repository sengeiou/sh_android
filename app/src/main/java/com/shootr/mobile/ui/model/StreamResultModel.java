package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class StreamResultModel implements Serializable {

    private StreamModel streamModel;
    private boolean isWatching;

    public StreamModel getStreamModel() {
        return streamModel;
    }

    public void setStreamModel(StreamModel streamModel) {
        this.streamModel = streamModel;
    }

    public void setIsWatching(boolean isWatching) {
        this.isWatching = isWatching;
    }

    public boolean isFollowing() {
        if (streamModel == null) {
            return false;
        }
        return streamModel.isFollowing();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StreamResultModel)) {
            return false;
        }

        StreamResultModel that = (StreamResultModel) o;

        return (streamModel != null && streamModel.getIdStream().equals(that.getStreamModel().getIdStream()));
    }

    @Override public int hashCode() {
        int result = streamModel != null ? streamModel.hashCode() : 0;
        result = 31 * result + (isWatching ? 1 : 0);
        return result;
    }
}
