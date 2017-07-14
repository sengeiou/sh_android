package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamModel;

public interface ShareStreamView {

  void renderStreamInfo(StreamModel streamModel);

  void shareStreamVia(StreamModel streamModel);
}
