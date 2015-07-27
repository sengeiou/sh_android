package com.shootr.android.domain.bus;

public interface WatchUpdateRequest {

    interface Receiver {

        void onWatchUpdateRequest(Stream stream);
    }

    class Stream {

    }
}
