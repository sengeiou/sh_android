package com.shootr.mobile.data.entity;

public class StreamSearchEntity extends StreamEntity {

    private int totalFollowingWatchers;

    public int getTotalFollowingWatchers() {
        return totalFollowingWatchers;
    }

    public void setTotalFollowingWatchers(int totalWatchers) {
        this.totalFollowingWatchers = totalWatchers;
    }
}
