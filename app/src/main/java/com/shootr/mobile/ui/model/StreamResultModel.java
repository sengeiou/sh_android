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

}
