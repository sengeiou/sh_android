package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.DiscoveredModel;

public interface OnDiscoverFavoriteClickListener {

  void onFavoriteClick(DiscoveredModel discoveredModel);

  void onRemoveFavoriteClick(DiscoveredModel discoveredModel);
}
