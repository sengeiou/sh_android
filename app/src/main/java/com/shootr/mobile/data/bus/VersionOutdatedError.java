package com.shootr.mobile.data.bus;

public interface VersionOutdatedError {

    interface Receiver {

        void onVersionOutdatedError(Event event);
    }

    class Event {

    }

}
