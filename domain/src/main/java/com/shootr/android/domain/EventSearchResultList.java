package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private String currentCheckedInEventId;
    private String currentWatchingEventId;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResultList(List<EventSearchResult> eventSearchResults, String currentCheckedInEventId,
      String currentWatchingEventId) {
        this.eventSearchResults = eventSearchResults;
        this.currentCheckedInEventId = currentCheckedInEventId;
        this.currentWatchingEventId = currentWatchingEventId;
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
    public String getCurrentWatchingEventId() {
        return currentWatchingEventId;
    }

    public void setCurrentCheckedInEventId(String currentCheckedInEventId) {
        this.currentCheckedInEventId = currentCheckedInEventId;
    }

    public void setCurrentWatchingEventId(String currentWatchingEventId) {
        this.currentWatchingEventId = currentWatchingEventId;
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
        return !(currentWatchingEventId != null ? !currentWatchingEventId.equals(that.currentWatchingEventId)
          : that.currentWatchingEventId != null);
    }

    @Override public int hashCode() {
        int result = eventSearchResults != null ? eventSearchResults.hashCode() : 0;
        result = 31 * result + (currentCheckedInEventId != null ? currentCheckedInEventId.hashCode() : 0);
        result = 31 * result + (currentWatchingEventId != null ? currentWatchingEventId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "EventSearchResultList{" +
          "eventSearchResults=" + eventSearchResults +
          ", currentCheckedInEventId='" + currentCheckedInEventId + '\'' +
          ", currentWatchingEventId='" + currentWatchingEventId + '\'' +
          '}';
    }
}
