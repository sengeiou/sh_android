package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.daimajia.swipe.SwipeLayout;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideHighlightShot;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class HighLightedShotViewHolder extends ShotTimelineViewHolder {

  private final OnHideHighlightShot onHideHighlightClickListener;

  @Bind(R.id.hide_highlighted) TextView hideHighlighted;
  @Bind(R.id.shot_container) View shotContainer;
  @Bind(R.id.dismiss_container) FrameLayout dismissContainer;
  @Bind(R.id.swipe) SwipeLayout swipeLayout;

  private HighlightedShotModel highlightedShotModel;

  public HighLightedShotViewHolder(View view, HighlightedShotModel highlightedShotModel,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnReplyShotListener onReplyShotListener,
      OnHideHighlightShot onHideHighlightClickListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ImageLoader imageLoader, ShotTextSpannableBuilder shotTextSpannableBuilder) {
    super(view, avatarClickListener, videoClickListener, onNiceShotListener, onReplyShotListener,
        onUsernameClickListener, timeUtils, imageLoader, shotTextSpannableBuilder);
    this.onHideHighlightClickListener = onHideHighlightClickListener;
    this.highlightedShotModel = highlightedShotModel;
  }

  public void renderHighLight(HighlightedShotModel highlightedShotModel, final ShotModel shotModel,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, Boolean isAdmin) {
    super.render(shotModel, shotClickListener, onShotLongClick, onLongClickListener,
        onTouchListener, onImageClickListener);
    setupSwipeLayout();
    setupListeners(highlightedShotModel, shotClickListener, onShotLongClick);

    if (isAdmin) {
      dismissContainer.setBackgroundColor(
          dismissContainer.getResources().getColor(R.color.dissmiss));
    } else {
      dismissContainer.setBackgroundColor(
          dismissContainer.getResources().getColor(R.color.gray_50));
    }

  }

  private void setupSwipeLayout() {
    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
  }

  private void setupListeners(final HighlightedShotModel highlightedShotModel,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick) {
    shotContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        shotClickListener.onClick(highlightedShotModel.getShotModel());
      }
    });
    shotContainer.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        onShotLongClick.onShotLongClick(highlightedShotModel.getShotModel());
        return false;
      }
    });
    hideHighlighted.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onHideHighlightClickListener.onHideClick(highlightedShotModel);
      }
    });
  }
}
