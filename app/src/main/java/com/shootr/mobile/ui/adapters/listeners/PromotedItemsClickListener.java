package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.PromotedLandingItemModel;

public interface PromotedItemsClickListener {

  void onImageClick(PromotedLandingItemModel promotedModel);

  void onFollowClick(PromotedLandingItemModel promotedModel);

  void onGoClick(PromotedLandingItemModel promotedModel);

  void onPromotedClick(PromotedLandingItemModel promotedModel);

}
