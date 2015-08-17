package com.shootr.android.domain.bus;

public interface WatchUpdateRequest {

    interface Receiver {

        void onWatchUpdateRequest(Event event);
    }

    class Event {

    }
}
