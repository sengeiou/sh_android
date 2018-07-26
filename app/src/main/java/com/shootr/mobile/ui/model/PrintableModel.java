package com.shootr.mobile.ui.model;

public interface PrintableModel {

  String ITEMS_GROUP = "ITEMS_GROUP";
  String FIXED_GROUP = "FIXED_GROUP";
  String PINNED_GROUP = "PINNED_GROUP";
  String MAIN_SHOT = "MAIN_SHOT";
  String REPLY = "REPLY";
  String PROMOTED_GROUP = "PROMOTED";
  String HIGHLIGHTED_GROUP = "HIGHLIGHTED_GROUP";
  String POLL_GROUP = "POLL_GROUP";
  String USER_GROUP = "USER_GROUP";


  String getTimelineGroup();

  void setTimelineGroup(String timelineGroup);

  Long getOrder();

  boolean isDeleted();
}
