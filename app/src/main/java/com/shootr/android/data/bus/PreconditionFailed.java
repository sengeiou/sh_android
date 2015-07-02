package com.shootr.android.data.bus;

public interface PreconditionFailed {

    interface Receiver {

        void onPreconditionFailed(Event event);
    }

    class Event {

    }

}
