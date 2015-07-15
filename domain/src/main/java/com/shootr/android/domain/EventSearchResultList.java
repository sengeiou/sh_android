package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private EventSearchResult currentWatchingEvent;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResultList(List<EventSearchResult> eventSearchResults, EventSearchResult currentWatchingEvent) {
        this.eventSearchResults = eventSearchResults;
        this.currentWatchingEvent = currentWatchingEvent;
    }

    public List<EventSearchResult> getEventSearchResults() {
        return eventSearchResults;
    }

    public void setEventSearchResults(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResult getCurrentWatchingEvent() {
        return currentWatchingEvent;
    }

    public void setCurrentWatchingEvent(EventSearchResult currentWatchingEvent) {
        this.currentWatchingEvent = currentWatchingEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSearchResultList)) return false;

        EventSearchResultList that = (EventSearchResultList) o;

        if (!eventSearchResults.equals(that.eventSearchResults)) return false;
        if (currentWatchingEvent != null ? !currentWatchingEvent.equals(that.currentWatchingEvent)
          : that.currentWatchingEvent != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventSearchResults.hashCode();
        result = 31 * result + (currentWatchingEvent != null ? currentWatchingEvent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventSearchResultList{" +
          "eventSearchResults=" + eventSearchResults +
          ", currentWatchingEvent=" + currentWatchingEvent +
          '}';
    }
}
