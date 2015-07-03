package com.shootr.android.data.bus;

public interface VersionOutdatedError {

    interface Receiver {

        void onVersionOutdatedError(Event event);
    }

    class Event {

    }

}
