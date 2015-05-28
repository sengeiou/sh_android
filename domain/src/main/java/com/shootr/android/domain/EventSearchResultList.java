package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private String currentCheckedInEventId;
    private EventSearchResult currentWatchingEvent;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResultList(List<EventSearchResult> eventSearchResults, String currentCheckedInEventId,
      EventSearchResult currentWatchingEvent) {
        this.eventSearchResults = eventSearchResults;
        this.currentCheckedInEventId = currentCheckedInEventId;
        this.currentWatchingEvent = currentWatchingEvent;
    }

    public List<EventSearchResult> getEventSearchResults() {
        return eventSearchResults;
    }

    public void setEventSearchResults(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public String getCurrentCheckedInEventId() {
        return currentCheckedInEventId;
    }

    public EventSearchResult getCurrentWatchingEvent() {
        return currentWatchingEvent;
    }

    public void setCurrentCheckedInEventId(String currentCheckedInEventId) {
        this.currentCheckedInEventId = currentCheckedInEventId;
    }

    public void setCurrentWatchingEvent(EventSearchResult currentWatchingEvent) {
        this.currentWatchingEvent = currentWatchingEvent;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSearchResultList)) return false;

        EventSearchResultList that = (EventSearchResultList) o;

        if (eventSearchResults != null ? !eventSearchResults.equals(that.eventSearchResults)
          : that.eventSearchResults != null) {
            return false;
        }
        if (currentCheckedInEventId != null ? !currentCheckedInEventId.equals(that.currentCheckedInEventId)
          : that.currentCheckedInEventId != null) {
            return false;
        }
        return !(currentWatchingEvent != null ? !currentWatchingEvent.equals(that.currentWatchingEvent)
          : that.currentWatchingEvent != null);
    }

    @Override public int hashCode() {
        int result = eventSearchResults != null ? eventSearchResults.hashCode() : 0;
        result = 31 * result + (currentCheckedInEventId != null ? currentCheckedInEventId.hashCode() : 0);
        result = 31 * result + (currentWatchingEvent != null ? currentWatchingEvent.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "EventSearchResultList{" +
          "eventSearchResults=" + eventSearchResults +
          ", currentCheckedInEventId='" + currentCheckedInEventId + '\'' +
          ", currentWatchingEvent='" + currentWatchingEvent + '\'' +
          '}';
    }
}
