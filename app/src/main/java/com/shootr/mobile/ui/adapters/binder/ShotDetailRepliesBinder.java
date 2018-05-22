package com.shootr.mobile.ui.adapters.binder;

import android.view.View;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDeleteHighlightedItemClick;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.BubbleShotBinder;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotDetailRepliesBinder extends BubbleShotBinder {

  public ShotDetailRepliesBinder(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, ShotClickListener shotClickListener,
      OnShotLongClick onShotLongClick, OnOpenShotMenuListener onOpenShotMenuListener,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener,
      OnDeleteHighlightedItemClick onHideHighlightClickListener,
      OnUrlClickListener onShotUrlClickListener, OnReshootClickListener onReshootClickListener,
      OnCtaClickListener onCtaClickListener, NumberFormatUtil numberFormatUtil) {
    super(imageLoader, avatarClickListener, videoClickListener, onNiceShotListener,
        onUsernameClickListener, timeUtils, shotTextSpannableBuilder, shotClickListener,
        onShotLongClick, onOpenShotMenuListener, onLongClickListener, onTouchListener,
        onImageClickListener, onHideHighlightClickListener, onShotUrlClickListener,
        onReshootClickListener, onCtaClickListener, numberFormatUtil);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof ShotModel && ((ShotModel) item).getTimelineGroup()
        .equals(PrintableModel.REPLY);
  }
}
