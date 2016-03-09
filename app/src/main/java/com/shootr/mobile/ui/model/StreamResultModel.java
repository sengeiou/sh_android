package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class StreamResultModel implements Serializable {

    private StreamModel streamModel;
    private int watchers;
    private boolean isWatching;

    public StreamModel getStreamModel() {
        return streamModel;
    }

    public void setStreamModel(StreamModel streamModel) {
        this.streamModel = streamModel;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public boolean isWatching() {
        return isWatching;
    }

    public void setIsWatching(boolean isWatching) {
        this.isWatching = isWatching;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamResultModel)) return false;

        StreamResultModel that = (StreamResultModel) o;

        if (watchers != that.watchers) return false;
        if (isWatching != that.isWatching) return false;
        return !(streamModel != null ? !streamModel.equals(that.streamModel) : that.streamModel != null);
    }

    @Override public int hashCode() {
        int result = streamModel != null ? streamModel.hashCode() : 0;
        result = 31 * result + watchers;
        result = 31 * result + (isWatching ? 1 : 0);
        return result;
    }
}
