package com.shootr.android.domain.bus;

public interface BadgeChanged {

    interface Receiver {

        void onBadgeChanged(Event event);

    }

    class Event {

    }

}
