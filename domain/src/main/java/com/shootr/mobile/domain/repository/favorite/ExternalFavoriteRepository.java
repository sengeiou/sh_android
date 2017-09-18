package com.shootr.mobile.domain.repository.favorite;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import java.util.List;

public interface ExternalFavoriteRepository extends FavoriteRepository {

  List<Favorite> getFavorites(String userId);

  List<OnBoarding> getOnBoardingStreams(String type, String locale);

  List<OnBoarding> getOnBoardingUsers(String type, String locale);

  void addSuggestedFavorites(List<String> idStreams);
}
