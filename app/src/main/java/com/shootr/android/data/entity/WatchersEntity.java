package com.shootr.android.data.entity;

public class WatchersEntity extends Synchronized {

    private String idStream;
    private Integer watchers;

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public Integer getWatchers() {
        return watchers;
    }

    public void setWatchers(Integer watchers) {
        this.watchers = watchers;
    }
}
