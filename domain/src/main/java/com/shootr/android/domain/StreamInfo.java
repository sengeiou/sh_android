package com.shootr.android.domain;

import java.util.List;

public class StreamInfo {

    private List<User> watchers;

    private User currentUserWatching;

    private Stream stream;

    private Integer numberOfFollowing;

    private Boolean hasMoreParticipants;

    public Boolean hasMoreParticipants() {
        return hasMoreParticipants;
    }

    public void setHasMoreParticipants(Boolean hasMoreParticipants) {
        this.hasMoreParticipants = hasMoreParticipants;
    }

    public Integer getNumberOfFollowing() {
        return numberOfFollowing;
    }

    public void setNumberOfFollowing(Integer numberOfFollowing) {
        this.numberOfFollowing = numberOfFollowing;
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

        public Builder numberOfFollowing(Integer numberOfFollowing) {
            streamInfo.setNumberOfFollowing(numberOfFollowing);
            return this;
        }

        public Builder hasMoreParticipants(Boolean hasMoreParticipants) {
            streamInfo.setHasMoreParticipants(hasMoreParticipants);
            return this;
        }

        public StreamInfo build() {
            return streamInfo;
        }

    }
}
