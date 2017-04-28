package com.shootr.mobile.domain.model.stream;

import com.shootr.mobile.domain.model.user.User;
import java.util.Comparator;

public class RecentSearch {

    private Long joinStreamDate;
    private Stream stream;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public static class StreamNameComparator implements Comparator<RecentSearch> {

        @Override public int compare(RecentSearch left, RecentSearch right) {
            return left.getJoinStreamDate().compareTo(right.getJoinStreamDate());
        }
    }
}
