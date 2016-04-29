package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.domain.Favorite;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FavoriteEntityMapper {

    @Inject public FavoriteEntityMapper() {
    }

    public FavoriteEntity transform(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdStream(favorite.getIdStream());
        entity.setOrder(favorite.getOrder());
        return entity;
    }

    public Favorite transform(FavoriteEntity entity) {
        if (entity == null) {
            return null;
        }
        Favorite favorite = new Favorite();
        favorite.setIdStream(entity.getIdStream());
        favorite.setOrder(entity.getOrder());
        return favorite;
    }

    public List<Favorite> transformEntities(List<FavoriteEntity> favoriteEntities) {
        List<Favorite> favorites = new ArrayList<>(favoriteEntities.size());
        for (FavoriteEntity favoriteEntity : favoriteEntities) {
            favorites.add(transform(favoriteEntity));
        }
        return favorites;
    }
}
