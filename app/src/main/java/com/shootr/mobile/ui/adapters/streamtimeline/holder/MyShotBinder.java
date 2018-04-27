package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.ShotType;
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
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;


public class MyShotBinder extends BubbleShotBinder {
  public MyShotBinder(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
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
    boolean canBind = false;
    if (item instanceof ShotModel) {
      canBind = ((ShotModel) item).getTimelineGroup().equals(PrintableModel.ITEMS_GROUP)
          && (((ShotModel) item).getType().equals(ShotType.COMMENT) || ((ShotModel) item).getType()
          .equals(ShotType.POLL)) && ((ShotModel) item).isMine();
    }

    return canBind;
  }

  @Override public BubbleShotViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.my_bubble_shot_item, parent, false);
    return new BubbleShotViewHolder(view, avatarClickListener, videoClickListener,
        onNiceShotListener, timeUtils,
        imageLoader, numberFormatUtil);
  }
}
