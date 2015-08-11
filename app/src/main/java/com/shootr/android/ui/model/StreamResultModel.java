package com.shootr.android.ui.model;

import java.io.Serializable;

public class StreamResultModel implements Serializable {

    private StreamModel streamModel;
    private int watchers;

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
}
