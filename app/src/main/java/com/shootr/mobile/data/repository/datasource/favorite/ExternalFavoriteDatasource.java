package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.FavoriteEntity;
import java.util.List;

public interface ExternalFavoriteDatasource extends FavoriteDataSource {

  List<FavoriteEntity> getFavorites(String userId);

}
