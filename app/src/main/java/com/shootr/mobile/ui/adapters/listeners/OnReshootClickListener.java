package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.ShotModel;

public interface OnReshootClickListener {

  void onReshootClick(ShotModel shot);

  void onUndoReshootClick(ShotModel shot);
}
