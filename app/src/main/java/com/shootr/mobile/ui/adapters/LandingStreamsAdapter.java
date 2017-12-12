package com.shootr.mobile.ui.adapters;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.shootr.mobile.ui.adapters.binder.StreamBinder;
import com.shootr.mobile.ui.adapters.listeners.OnLandingStreamClickListener;
import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class LandingStreamsAdapter extends RecyclerAdapter {

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final OnLandingStreamClickListener onStreamClickListener;

  private DataListManager<StreamModel> userStreams;
  private DataListManager<StreamModel> hotStreams;

  public LandingStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnLandingStreamClickListener onStreamClickListener) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;

    setupList();
  }

  private void setupList() {
    userStreams = new DataListManager<StreamModel>(this);
    hotStreams = new DataListManager<StreamModel>(this);

    addDataManager(userStreams);
    addDataManager(hotStreams);

    registerBinder(new StreamBinder(imageLoader, initialsLoader, onStreamClickListener));
  }

  public void setStreams(LandingStreamsModel landingStreams) {
    userStreams.addAll(landingStreams.getUserStreams());
    hotStreams.addAll(landingStreams.getHotStreams());
  }
}
