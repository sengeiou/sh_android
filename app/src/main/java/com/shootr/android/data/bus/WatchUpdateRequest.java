package com.shootr.android.data.bus;

public interface WatchUpdateRequest {

    interface Receiver {

        void onWatchUpdateRequest(Event event);
    }

    class Event {

    }
}
