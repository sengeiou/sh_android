package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
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
import com.shootr.mobile.ui.adapters.streamtimeline.holder.BubbleShotDetailViewHolder;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;

public class ShotDetailBinder extends ItemBinder<ShotModel, BubbleShotDetailViewHolder> {

  protected final ImageLoader imageLoader;
  protected final OnAvatarClickListener avatarClickListener;
  protected final OnVideoClickListener videoClickListener;
  protected final OnNiceShotListener onNiceShotListener;
  protected final OnUsernameClickListener onUsernameClickListener;
  protected final AndroidTimeUtils timeUtils;
  protected final ShotTextSpannableBuilder shotTextSpannableBuilder;
  protected final ShotClickListener shotClickListener;
  protected final OnShotLongClick onShotLongClick;
  protected final OnOpenShotMenuListener onOpenShotMenuListener;
  protected final OnImageLongClickListener onLongClickListener;
  protected final View.OnTouchListener onTouchListener;
  protected final OnImageClickListener onImageClickListener;
  protected final OnUrlClickListener onShotUrlClickListener;
  protected final OnReshootClickListener onReshootClickListener;
  protected final OnCtaClickListener onCtaClickListener;
  protected final NumberFormatUtil numberFormatUtil;
  protected final TimeFormatter timeFormatter;

  public ShotDetailBinder(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, ShotClickListener shotClickListener,
      OnShotLongClick onShotLongClick, OnOpenShotMenuListener onOpenShotMenuListener,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener,
      OnUrlClickListener onShotUrlClickListener, OnReshootClickListener onReshootClickListener,
      OnCtaClickListener onCtaClickListener, NumberFormatUtil numberFormatUtil,
      TimeFormatter timeFormatter) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.onImageClickListener = onImageClickListener;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.onCtaClickListener = onCtaClickListener;
    this.numberFormatUtil = numberFormatUtil;
    this.timeFormatter = timeFormatter;
  }

  @Override public BubbleShotDetailViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_main_shot_detail_item, parent, false);

    return new BubbleShotDetailViewHolder(view, avatarClickListener, videoClickListener,
        onNiceShotListener, timeFormatter, imageLoader, numberFormatUtil, onShotUrlClickListener,
        onImageClickListener);
  }

  @Override public void bind(BubbleShotDetailViewHolder holder, ShotModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof ShotModel && ((ShotModel) item).getTimelineGroup()
        .equals(PrintableModel.MAIN_SHOT);
  }
}
