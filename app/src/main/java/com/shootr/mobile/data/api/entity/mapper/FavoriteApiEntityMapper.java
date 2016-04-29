package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.FavoriteApiEntity;
import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FavoriteApiEntityMapper {

    @Inject public FavoriteApiEntityMapper() {
    }

    public FavoriteEntity transform(FavoriteApiEntity apiEntity) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdStream(apiEntity.getIdStream());
        entity.setOrder(apiEntity.getOrder());
        entity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
        return entity;
    }

    public List<FavoriteEntity> transform(List<FavoriteApiEntity> favorites) {
        List<FavoriteEntity> favoriteEntities = new ArrayList<>(favorites.size());
        for (FavoriteApiEntity favorite : favorites) {
            favoriteEntities.add(transform(favorite));
        }
        return favoriteEntities;
    }
}
