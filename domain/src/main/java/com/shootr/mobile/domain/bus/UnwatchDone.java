package com.shootr.mobile.domain.bus;

public interface UnwatchDone {

    interface Receiver {

        void onUnwatchDone(Event event);
    }

    class Event {

    }
}
