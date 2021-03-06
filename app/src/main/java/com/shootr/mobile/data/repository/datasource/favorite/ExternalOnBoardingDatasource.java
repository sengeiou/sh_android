package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.OnBoardingEntity;
import java.util.List;

public interface ExternalOnBoardingDatasource {

  List<OnBoardingEntity> getOnBoarding(String type, String locale);

  void addFavorites(List<String> idOnBoardings, String type);

}
