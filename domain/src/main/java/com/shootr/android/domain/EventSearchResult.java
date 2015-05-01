package com.shootr.android.domain;

public class EventSearchResult {

    private Event event;
    private int watchersNumber;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getWatchersNumber() {
        return watchersNumber;
    }

    public void setWatchersNumber(int watchersNumber) {
        this.watchersNumber = watchersNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSearchResult)) return false;

        EventSearchResult that = (EventSearchResult) o;

        if (watchersNumber != that.watchersNumber) return false;
        return event.equals(that.event);
    }

    @Override public int hashCode() {
        int result = event.hashCode();
        result = 31 * result + watchersNumber;
        return result;
    }

    @Override public String toString() {
        return "EventSearchResult{" +
          "event=" + event +
          ", watchersNumber=" + watchersNumber +
          '}';
    }
}
