package com.shootr.android.domain.repository;

import com.shootr.android.domain.Watch;

public interface WatchRepository {

    void putWatch(Watch watch, ErrorCallback callback);
}
