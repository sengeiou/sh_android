package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.FavoriteEntity;
import java.util.List;

public interface InternalFavoriteDatasource extends FavoriteDataSource {

  List<FavoriteEntity> getFavorites();

  FavoriteEntity getFavoriteByIdStream(String idStream);

  void clear();

}
