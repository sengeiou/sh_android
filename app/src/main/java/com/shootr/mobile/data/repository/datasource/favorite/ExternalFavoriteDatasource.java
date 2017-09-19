package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.OnBoardingEntity;
import java.util.List;

public interface ExternalFavoriteDatasource extends FavoriteDataSource {

  List<FavoriteEntity> getFavorites(String userId);

  List<OnBoardingEntity> getOnBoarding(String type, String locale);

  void addFavorites(List<String> idOnBoardings, String type);

}
