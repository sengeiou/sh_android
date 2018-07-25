package com.shootr.mobile.data.entity;

public class LandingStreamsEntity {

  private UserStreamsEntity userStreams;
  private HotEntity hot;
  private PromotedItemsEntity promoted;

  public UserStreamsEntity getUserStreams() {
    return userStreams;
  }

  public void setUserStreams(UserStreamsEntity userStreams) {
    this.userStreams = userStreams;
  }

  public HotEntity getHot() {
    return hot;
  }

  public void setHot(HotEntity hot) {
    this.hot = hot;
  }

  public PromotedItemsEntity getPromoted() {
    return promoted;
  }

  public void setPromoted(PromotedItemsEntity promoted) {
    this.promoted = promoted;
  }
}
