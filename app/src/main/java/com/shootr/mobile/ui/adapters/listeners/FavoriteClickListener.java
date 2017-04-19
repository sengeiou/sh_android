package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamModel;

public interface FavoriteClickListener {

  void onFavoriteClick(StreamModel stream);

  void onRemoveFavoriteClick(StreamModel stream);
}
