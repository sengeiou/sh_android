package com.shootr.android.domain.bus;

public interface FavoriteAdded {

    interface Receiver {

        void onFavoriteAdded(Event event);

    }

    class Event {

    }

}
