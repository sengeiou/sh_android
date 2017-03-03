package com.shootr.mobile.domain.repository.favorite;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.OnBoardingStream;
import java.util.List;

public interface ExternalFavoriteRepository extends FavoriteRepository {

  List<Favorite> getFavorites(String userId);

  List<OnBoardingStream> getOnBoardingStreams(String locale);

  void addSuggestedFavorites(List<String> idStreams);
}
