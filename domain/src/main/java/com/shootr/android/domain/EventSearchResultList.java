package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private String currentCheckedInEventId;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResultList(List<EventSearchResult> eventSearchResults, String currentCheckedInEventId) {
        this.eventSearchResults = eventSearchResults;
        this.currentCheckedInEventId = currentCheckedInEventId;
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

    public void setCurrentCheckedInEventId(String currentCheckedInEventId) {
        this.currentCheckedInEventId = currentCheckedInEventId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSearchResultList)) return false;

        EventSearchResultList that = (EventSearchResultList) o;

        if (eventSearchResults != null ? !eventSearchResults.equals(that.eventSearchResults)
          : that.eventSearchResults != null) {
            return false;
        }
        return !(currentCheckedInEventId != null ? !currentCheckedInEventId.equals(that.currentCheckedInEventId)
          : that.currentCheckedInEventId != null);
    }

    @Override public int hashCode() {
        int result = eventSearchResults != null ? eventSearchResults.hashCode() : 0;
        result = 31 * result + (currentCheckedInEventId != null ? currentCheckedInEventId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "EventSearchResultList{" +
          "eventSearchResults=" + eventSearchResults +
          ", currentCheckedInEventId=" + currentCheckedInEventId +
          '}';
    }
}
