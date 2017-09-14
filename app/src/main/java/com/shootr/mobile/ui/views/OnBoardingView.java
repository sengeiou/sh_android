package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.OnBoardingModel;
import java.util.List;

public interface OnBoardingView {

  void showLoading();

  void hideLoading();

  void renderOnBoardingList(List<OnBoardingModel> onBoardingStreamModels);

  void sendStreamAnalytics(String idStream, String streamTitle, boolean isStrategic);

  void goToUserOnboardingList();

  void hideGetStarted();

  void showError(String errorMessage);
}