package com.shootr.android.domain;

import java.util.List;

public class StreamInfo {

    private List<User> watchers;

    private User currentUserWatching;

    private Stream stream;

    public int getWatchersCount() {
        return currentUserWatching != null ? watchers.size() + 1 : watchers.size();
    }

    public List<User> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<User> watchers) {
        this.watchers = watchers;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public static Builder builder() {
        return new Builder();
    }

    public User getCurrentUserWatching() {
        return currentUserWatching;
    }

    public void setCurrentUserWatching(User currentUserWatching) {
        this.currentUserWatching = currentUserWatching;
    }

    public static class Builder {

        private StreamInfo streamInfo;

        public Builder() {
            this.streamInfo = new StreamInfo();
        }

        public Builder watchers(List<User> watches) {
            streamInfo.setWatchers(watches);
            return this;
        }

        public Builder currentUserWatching(User currentUserWatching) {
            streamInfo.setCurrentUserWatching(currentUserWatching);
            return this;
        }

        public Builder stream(Stream stream) {
            streamInfo.setStream(stream);
            return this;
        }

        public StreamInfo build() {
            return streamInfo;
        }
    }
}
