package com.shootr.android.domain;

import java.util.List;

public class EventSearchResultList {

    private List<EventSearchResult> eventSearchResults;
    private Event currentVisibleEvent;

    public EventSearchResultList(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public List<EventSearchResult> getEventSearchResults() {
        return eventSearchResults;
    }

    public void setEventSearchResults(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    public Event getCurrentVisibleEvent() {
        return currentVisibleEvent;
    }

    public void setCurrentVisibleEvent(Event currentVisibleEvent) {
        this.currentVisibleEvent = currentVisibleEvent;
    }
}
