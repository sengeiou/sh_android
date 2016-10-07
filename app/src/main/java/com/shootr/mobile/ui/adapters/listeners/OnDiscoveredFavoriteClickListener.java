package com.shootr.mobile.ui.adapters.listeners;

public interface OnDiscoveredFavoriteClickListener {

  void onFavoriteClick(String idStream, String streamTitle);

  void onRemoveFavoriteClick(String idStream);
}
