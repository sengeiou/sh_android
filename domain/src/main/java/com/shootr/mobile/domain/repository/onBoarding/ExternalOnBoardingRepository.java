package com.shootr.mobile.domain.repository.onBoarding;

import com.shootr.mobile.domain.model.stream.OnBoarding;
import java.util.List;

public interface ExternalOnBoardingRepository {

  List<OnBoarding> getOnBoardingStreams(String type, String locale);

  List<OnBoarding> getOnBoardingUsers(String type, String locale);

  void addSuggestedFavorites(List<String> idOnBoadrings, String type);
}
