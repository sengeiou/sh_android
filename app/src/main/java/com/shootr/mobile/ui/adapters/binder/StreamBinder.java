package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.StreamViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnLandingStreamClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class StreamBinder extends ItemBinder<StreamModel, StreamViewHolder> {

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final OnLandingStreamClickListener onStreamClickListener;

  public StreamBinder(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnLandingStreamClickListener onStreamClickListener) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;
  }

  @Override public StreamViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landing_stream, parent, false);
    return new StreamViewHolder(view, onStreamClickListener, imageLoader, initialsLoader);
  }

  @Override public void bind(StreamViewHolder holder, StreamModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof StreamModel;
  }
}
