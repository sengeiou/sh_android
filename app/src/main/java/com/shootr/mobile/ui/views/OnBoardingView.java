package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.OnBoardingStreamModel;
import java.util.List;

public interface OnBoardingView {

  void showLoading();

  void hideLoading();

  void renderOnBoardingList(List<OnBoardingStreamModel> onBoardingStreamModels);

  void sendAnalytics(String idStream, String streamTitle, boolean isStrategic);

  void goToStreamList();

  void hideGetStarted();

  void showError(String errorMessage);
}