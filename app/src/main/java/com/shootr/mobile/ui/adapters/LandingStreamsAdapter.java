package com.shootr.mobile.ui.adapters;

import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.shootr.mobile.ui.adapters.binder.HotElementBinder;
import com.shootr.mobile.ui.adapters.binder.ListElement;
import com.shootr.mobile.ui.adapters.binder.SeparatorElementBinder;
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
  private DataListManager<ListElement> separator;
  private DataListManager<ListElement> header;

  public LandingStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnLandingStreamClickListener onStreamClickListener) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;

    setupList();
  }

  private void setupList() {
    userStreams = new DataListManager<>(this);
    hotStreams = new DataListManager<>(this);
    separator = new DataListManager<>(this);
    header = new DataListManager<>(this);

    addDataManager(userStreams);
    addDataManager(separator);
    addDataManager(header);
    addDataManager(hotStreams);

    registerBinder(new StreamBinder(imageLoader, initialsLoader, onStreamClickListener));
    registerBinder(new SeparatorElementBinder());
    registerBinder(new HotElementBinder());
  }

  public void setStreams(LandingStreamsModel landingStreams) {
    userStreams.clear();
    hotStreams.clear();
    separator.clear();
    header.clear();

    hotStreams.addAll(landingStreams.getHotStreams());
    header.add(new ListElement(ListElement.HEADER));
    userStreams.addAll(landingStreams.getUserStreams());
    if (!userStreams.isEmpty()) {
      separator.add(new ListElement(ListElement.SEPARATOR));
    }
  }
}
