package com.shootr.android.domain;

import java.util.List;

public class EventInfo {

    private Watch currentUserWatch;

    private List<Watch> watchers;

    private Event event;

    public int getWatchersCount() {
        int watchers = this.watchers.size();
        return watchers + 1;
    }

    public Watch getCurrentUserWatch() {
        return currentUserWatch;
    }

    public void setCurrentUserWatch(Watch currentUserWatch) {
        this.currentUserWatch = currentUserWatch;
    }

    public List<Watch> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<Watch> watchers) {
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

    public static class Builder {

        private EventInfo eventInfo;

        public Builder() {
            this.eventInfo = new EventInfo();
        }

        public Builder currentUserWatch(Watch currentUserWatch) {
            eventInfo.setCurrentUserWatch(currentUserWatch);
            return this;
        }

        public Builder watchers(List<Watch> watches) {
            eventInfo.setWatchers(watches);
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
