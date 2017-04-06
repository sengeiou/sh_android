package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamModel;

public interface SearchStreamView {

  void hideKeyboard();

  void navigateToStreamTimeline(String idStream, String streamTitle, String authorId);

  void showAddedToFavorites(StreamModel streamModel);

  void showRemovedFromFavorites(StreamModel streamModel);

  void showStreamShared();

  void showError(String errorMessage);
}
