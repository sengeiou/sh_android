package com.shootr.mobile.ui.widgets;

import android.view.View;

/**
 * Created by miniserver on 21/3/17.
 */
public interface BaseMessagePressableSpan {
  void setPressed(boolean isPressed);

  void onClick(View widget);
}
