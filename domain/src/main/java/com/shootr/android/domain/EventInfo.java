package com.shootr.android.domain;

import java.util.List;

public class EventInfo {

    private List<User> watchers;

    private User currentUserWatching;

    private Event event;

    public int getWatchersCount() {
        return currentUserWatching != null ? watchers.size() + 1 : watchers.size();
    }

    public List<User> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<User> watchers) {
        this.watchers = watchers;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

        private EventInfo eventInfo;

        public Builder() {
            this.eventInfo = new EventInfo();
        }

        public Builder watchers(List<User> watches) {
            eventInfo.setWatchers(watches);
            return this;
        }

        public Builder currentUserWatching(User currentUserWatching) {
            eventInfo.setCurrentUserWatching(currentUserWatching);
            return this;
        }

        public Builder event(Event event) {
            eventInfo.setEvent(event);
            return this;
        }

        public EventInfo build() {
            return eventInfo;
        }
    }
}
