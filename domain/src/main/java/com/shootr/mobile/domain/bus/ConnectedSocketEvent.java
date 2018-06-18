package com.shootr.mobile.domain.bus;

public interface ConnectedSocketEvent {

    interface Receiver {

        void onRestoreEvent(Event event);
    }

    class Event {

    }
}
