package com.shootr.android.domain.bus;

public interface UnwatchDone {

    interface Receiver {

        void onUnwatchDone(Event event);

    }

    class Event {

    }

}
