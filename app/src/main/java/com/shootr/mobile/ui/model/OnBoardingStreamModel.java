package com.shootr.mobile.ui.model;

public class OnBoardingStreamModel {

  private StreamModel streamModel;
  private boolean isFavorite;

  public StreamModel getStreamModel() {
    return streamModel;
  }

  public void setStreamModel(StreamModel streamModel) {
    this.streamModel = streamModel;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite = favorite;
  }
}
