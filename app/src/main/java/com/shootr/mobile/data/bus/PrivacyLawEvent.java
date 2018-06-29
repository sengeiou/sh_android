package com.shootr.mobile.data.bus;

public interface PrivacyLawEvent {

    interface Receiver {

        void onPrivacyLaw(Event event);
    }

    class Event {

    }
}
