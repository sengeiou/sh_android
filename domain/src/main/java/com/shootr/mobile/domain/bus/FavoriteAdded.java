package com.shootr.mobile.domain.bus;

public interface FavoriteAdded {

    interface Receiver {

        void onFavoriteAdded(Event event);

    }

    class Event {

    }

}
