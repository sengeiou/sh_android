package com.shootr.mobile.data.bus;

public interface UpdateWarning {

    interface Receiver {

        void onUpdateWarning(Event event);
    }

    class Event {

    }
}
