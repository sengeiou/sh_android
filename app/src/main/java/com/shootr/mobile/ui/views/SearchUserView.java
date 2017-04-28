package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;

public interface SearchUserView {

  void hideKeyboard();

  void showContent();

  void hideContent();

  void showFollow(UserModel userModel);

  void showUnfollow(UserModel userModel);

  void showError(String errorMessage);
}
