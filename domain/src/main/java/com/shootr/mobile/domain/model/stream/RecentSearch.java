package com.shootr.mobile.domain.model.stream;

import java.util.Comparator;

public class RecentStream {

    private Long joinStreamDate;
    private Stream stream;

    public Long getJoinStreamDate() {
        return joinStreamDate;
    }

    public void setJoinStreamDate(Long joinStreamDate) {
        this.joinStreamDate = joinStreamDate;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public static class StreamNameComparator implements Comparator<RecentStream> {

        @Override public int compare(RecentStream left, RecentStream right) {
            return left.getJoinStreamDate().compareTo(right.getJoinStreamDate());
        }
    }
}
