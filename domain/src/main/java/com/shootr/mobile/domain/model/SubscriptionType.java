package com.shootr.mobile.domain.model;

public interface SubscriptionType {

  String TIMELINE = "TIMELINE";
  String SHOT_DETAIL = "SHOT_DETAIL";
  String PROMOTED_RECEIPT = "PROMOTED_RECEIPT";

  String[] PERSISTENT_SUBSCRIPTIONS = {PROMOTED_RECEIPT};
}
