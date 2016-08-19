package com.shootr.mobile.domain.repository.favorite;

import com.shootr.mobile.domain.model.stream.Favorite;
import java.util.List;

public interface InternalFavoriteRepository extends FavoriteRepository {

  List<Favorite> getFavorites();

  Favorite getFavoriteByStream(String idStream);
}
