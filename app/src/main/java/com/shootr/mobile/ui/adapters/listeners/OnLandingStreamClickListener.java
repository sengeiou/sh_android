package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamModel;

public abstract class OnLandingStreamClickListener {

  public abstract void onStreamClick(StreamModel stream);

  public boolean onStreamLongClick(StreamModel stream) {
    return false;
  }
}
