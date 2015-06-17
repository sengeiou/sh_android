package com.shootr.android.data.api.entity.mapper;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.Synchronized;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class FavoriteApiEntityMapper {

    @Inject public FavoriteApiEntityMapper() {
    }

    public FavoriteEntity transform(FavoriteApiEntity apiEntity) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdEvent(apiEntity.getIdEvent());
        entity.setOrder(apiEntity.getOrder());
        entity.setBirth(toDate(apiEntity.getBirth()));
        entity.setModified(toDate(apiEntity.getModified()));
        entity.setDeleted(toDate(apiEntity.getDeleted()));
        entity.setRevision(apiEntity.getRevision());
        entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
        return entity;
    }

    public List<FavoriteEntity> transform(List<FavoriteApiEntity> favorites) {
        List<FavoriteEntity> favoriteEntities = new ArrayList<>(favorites.size());
        for (FavoriteApiEntity favorite : favorites) {
            favoriteEntities.add(transform(favorite));
        }
        return favoriteEntities;
    }

    private Date toDate(Long timestamp) {
        return timestamp != null ? new Date(timestamp) : null;
    }
}
