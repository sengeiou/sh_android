package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.WatchersEntity;
import com.shootr.android.domain.Watchers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class WatchersEntityMapper {

    @Inject public WatchersEntityMapper() {
    }

    public Watchers transform(WatchersEntity watchersEntity) {
        if (watchersEntity == null) {
            return null;
        }
        Watchers watchers = new Watchers();
        watchers.setIdStream(watchersEntity.getIdStream());
        watchers.setWatchers(watchersEntity.getWatchers());
        return watchers;
    }

    public List<Watchers> transform(List<WatchersEntity> watchersEntities) {
        List<Watchers> watchersList = new ArrayList<>();
        for (WatchersEntity watchersEntity : watchersEntities) {
            watchersList.add(transform(watchersEntity));
        }
        return watchersList;
    }
}
