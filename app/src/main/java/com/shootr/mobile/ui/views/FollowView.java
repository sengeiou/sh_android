package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.FollowModel;
import com.shootr.mobile.ui.model.UserModel;

public interface FollowView {

  void showContent();

  void hideContent();

  void showFollow(UserModel userModel);

  void showUnfollow(UserModel userModel);

  void showError(String errorMessage);

  void showNoFollowing();

  void showProgressView();

  void hideProgressView();

  void registerAnalytics(boolean followers);

  void renderItems(FollowModel followings);
}
