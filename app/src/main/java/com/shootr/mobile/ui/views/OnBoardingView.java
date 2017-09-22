package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public interface OnBoardingView {

  void showLoading();

  void hideLoading();

  void renderOnBoardingList(List<OnBoardingModel> onBoardingModels);

  void sendStreamAnalytics(String idStream, String streamTitle, boolean isStrategic);

  void sendUserAnalytics(UserModel userModel);

  void goNextScreen();

  void hideGetStarted();

  void showError(String errorMessage);
}