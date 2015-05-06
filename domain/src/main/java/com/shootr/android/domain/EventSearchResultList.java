package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private Long currentVisibleEventId;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public EventSearchResultList(List<EventSearchResult> eventSearchResults, Long currentVisibleEventId) {
        this.eventSearchResults = eventSearchResults;
        this.currentVisibleEventId = currentVisibleEventId;
    }

    public List<EventSearchResult> getEventSearchResults() {
        return eventSearchResults;
    }

    public void setEventSearchResults(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public Long getCurrentVisibleEventId() {
        return currentVisibleEventId;
    }

    public void setCurrentVisibleEventId(Long currentVisibleEventId) {
        this.currentVisibleEventId = currentVisibleEventId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSearchResultList)) return false;

        EventSearchResultList that = (EventSearchResultList) o;

        if (eventSearchResults != null ? !eventSearchResults.equals(that.eventSearchResults)
          : that.eventSearchResults != null) {
            return false;
        }
        return !(currentVisibleEventId != null ? !currentVisibleEventId.equals(that.currentVisibleEventId)
          : that.currentVisibleEventId != null);
    }

    @Override public int hashCode() {
        int result = eventSearchResults != null ? eventSearchResults.hashCode() : 0;
        result = 31 * result + (currentVisibleEventId != null ? currentVisibleEventId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "EventSearchResultList{" +
          "eventSearchResults=" + eventSearchResults +
          ", currentVisibleEventId=" + currentVisibleEventId +
          '}';
    }
}
