package com.shootr.android.data.repository.datasource;

import com.shootr.android.data.entity.WatchEntity;

public interface WatchDataSource {

    public WatchEntity getWatch(Long idEvent, Long idUser);

    public WatchEntity putWatch(WatchEntity watchEntity);

    WatchEntity getWatching(Long userId);
}
