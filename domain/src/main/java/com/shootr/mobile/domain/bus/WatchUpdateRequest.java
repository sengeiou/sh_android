package com.shootr.mobile.domain.bus;

public interface WatchUpdateRequest {

    interface Receiver {

        void onWatchUpdateRequest(Event event);
    }

    class Event {

    }
}
