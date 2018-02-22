package com.shootr.mobile.domain.bus;

public interface CloseSocketEvent {

    interface Receiver {

        void onEvent(Event event);
    }

    class Event {

    }
}
