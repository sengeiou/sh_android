package com.shootr.mobile.data.bus;

public interface ServerDown {

    interface Receiver {

        void onServerDown(Event event);
    }

    class Event {

    }
}
