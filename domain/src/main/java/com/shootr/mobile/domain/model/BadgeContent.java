package com.shootr.mobile.domain.model;


public class BadgeContent {

  public static final String TIMELINE_TYPE = "TIMELINE";

  private String badgeType;
  private String filter;

  public String getBadgeType() {
    return badgeType;
  }

  public void setBadgeType(String badgeType) {
    this.badgeType = badgeType;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }
}
