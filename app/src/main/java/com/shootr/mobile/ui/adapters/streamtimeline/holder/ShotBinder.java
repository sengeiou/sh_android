package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
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
import java.util.List;

public class ShotBinder extends ItemBinder<ShotModel, ShotViewHolder> {

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
  protected final OnDeleteHighlightedItemClick onHideHighlightClickListener;
  protected final OnUrlClickListener onShotUrlClickListener;
  protected final OnReshootClickListener onReshootClickListener;
  protected final OnCtaClickListener onCtaClickListener;
  protected final NumberFormatUtil numberFormatUtil;

  public ShotBinder(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, ShotClickListener shotClickListener,
      OnShotLongClick onShotLongClick, OnOpenShotMenuListener onOpenShotMenuListener,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, OnDeleteHighlightedItemClick onHideHighlightClickListener,
      OnUrlClickListener onShotUrlClickListener,
      OnReshootClickListener onReshootClickListener, OnCtaClickListener onCtaClickListener,
      NumberFormatUtil numberFormatUtil) {
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
    this.onHideHighlightClickListener = onHideHighlightClickListener;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.onCtaClickListener = onCtaClickListener;
    this.numberFormatUtil = numberFormatUtil;
  }

  @Override public ShotViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_swipeable_shot_timeline, parent, false);
    return new ShotViewHolder(view, avatarClickListener, videoClickListener,
        onNiceShotListener, timeUtils,
        imageLoader, numberFormatUtil);
  }

  @Override public void bind(ShotViewHolder holder, ShotModel item) {
    holder.render(item, shotClickListener, onShotLongClick, onLongClickListener, onTouchListener,
        onImageClickListener, onReshootClickListener, onShotUrlClickListener, onOpenShotMenuListener);
  }

  @Override public boolean canBindData(Object item) {
    boolean canBind = false;
    if (item instanceof ShotModel) {
      canBind = ((ShotModel) item).getTimelineGroup().equals(PrintableModel.ITEMS_GROUP)
          && (((ShotModel) item).getType().equals(ShotType.COMMENT) || ((ShotModel) item).getType()
          .equals(ShotType.POLL));
    }

    return canBind;
  }

  @Override public void bind(ShotViewHolder holder, ShotModel item, List payloads) {
    Bundle bundle = (Bundle) payloads.get(0);

    for (String key : bundle.keySet()) {
      if (key.equals("nice")) {
        holder.setNiceCount(bundle.getInt("nice"));
      }
    }

  }
}
