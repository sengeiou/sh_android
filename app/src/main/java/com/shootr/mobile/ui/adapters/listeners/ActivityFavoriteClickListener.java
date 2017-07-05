package com.shootr.mobile.ui.adapters.listeners;

public interface ActivityFavoriteClickListener {

  void onFavoriteClick(String idStream, String streamTitle, boolean isStrategic);

  void onRemoveFavoriteClick(String idStream);
}
