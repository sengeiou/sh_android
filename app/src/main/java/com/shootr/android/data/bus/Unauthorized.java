package com.shootr.android.data.bus;

public interface Unauthorized {

    interface Receiver {

        void onUnauthorized(Event event);
    }

    class Event {

    }
}
