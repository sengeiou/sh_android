package com.shootr.android.data.api.entity;

public class WatchersApiEntity {

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
