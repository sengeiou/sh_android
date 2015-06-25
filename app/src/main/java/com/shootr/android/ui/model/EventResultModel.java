package com.shootr.android.ui.model;

import java.io.Serializable;

public class EventResultModel implements Serializable {

    private EventModel eventModel;
    private int watchers;

    public EventModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }
}
