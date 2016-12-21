package com.shootr.mobile.ui.adapters.listeners;

import android.view.View;
import com.shootr.mobile.ui.model.BaseMessageModel;

public interface OnImageClickListener {

  void onImageClick(View sharedImage, BaseMessageModel shot);
}
