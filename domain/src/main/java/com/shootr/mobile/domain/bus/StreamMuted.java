package com.shootr.mobile.domain.bus;

public interface StreamMuted {

    interface Receiver {

        void onStreamMuted(Event event);
    }

    class Event {

    }
}
