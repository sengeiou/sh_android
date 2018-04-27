package com.shootr.mobile.ui.model;

public interface PrintableModel {

  String ITEMS_GROUP = "ITEMS_GROUP";
  String FIXED_GROUP = "FIXED_GROUP";
  String PINNED_GROUP = "PINNED_GROUP";


  String getTimelineGroup();

  void setTimelineGroup(String timelineGroup);

  Long getOrder();
}
