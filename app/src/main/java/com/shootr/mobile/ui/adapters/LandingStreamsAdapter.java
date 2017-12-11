package com.shootr.mobile.ui.adapters;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnLandingStreamClickListener;
import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class LandingStreamsAdapter extends RecyclerAdapter {

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final OnLandingStreamClickListener onStreamClickListener;
  private final OnFavoriteClickListener onFavoriteClickListener;

  private DataListManager<StreamModel> userStreams;
  private DataListManager<StreamModel> hotStreams;

  public LandingStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnLandingStreamClickListener onStreamClickListener, OnFavoriteClickListener onFavoriteClickListener) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  private void setupList() {
    userStreams = new DataListManager<StreamModel>(this);
    hotStreams = new DataListManager<StreamModel>(this);
  }

  public void setStreams(LandingStreamsModel landingStreams) {
    userStreams.addAll(landingStreams.getUserStreams());
    hotStreams.addAll(landingStreams.getHotStreams());
  }
}
