package com.shootr.android.data.repository.datasource.watch;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface WatchDataSource extends SyncableDataSource<WatchEntity> {

    public WatchEntity putWatch(WatchEntity watchEntity);

    List<WatchEntity> putWatches(List<WatchEntity> watchEntities);

    public WatchEntity getWatch(Long idEvent, Long idUser);

    WatchEntity getVisible(Long userId);

    List<WatchEntity> getWatchesForUsersAndEvent(List<Long> userIds, Long idEvent);

    List<WatchEntity> getWatchesFromUsers(List<Long> userIds);

    void deleteAllWatchesNotPending();
}
