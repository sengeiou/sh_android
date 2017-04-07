package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamModel;

public interface OnDiscoverTimelineFavoriteClickListener {

  void onFavoriteClick(StreamModel streamModel);

  void onRemoveFavoriteClick(StreamModel streamModel);
}
