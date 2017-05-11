package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.daimajia.swipe.SwipeLayout;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideHighlightShot;
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
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class HighLightedShotViewHolder extends ShotTimelineViewHolder {

  protected final OnHideHighlightShot onHideHighlightClickListener;

  @BindView(R.id.hide_highlighted) TextView hideHighlighted;
  @BindView(R.id.shot_container) View shotContainer;
  @BindView(R.id.dismiss_container) FrameLayout dismissContainer;
  @BindView(R.id.swipe) SwipeLayout swipeLayout;
  @BindView(R.id.open_menu) LinearLayout openHighlightedMenu;
  @BindView(R.id.open_menu_container) FrameLayout openMenuContainer;

  public HighLightedShotViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnOpenShotMenuListener onOpenShotMenuListener,
      OnHideHighlightShot onHideHighlightClickListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ImageLoader imageLoader, ShotTextSpannableBuilder shotTextSpannableBuilder,
      NumberFormatUtil numberFormatUtil) {
    super(view, avatarClickListener, videoClickListener, onNiceShotListener, onOpenShotMenuListener,
        onUsernameClickListener, timeUtils, imageLoader, numberFormatUtil,
        shotTextSpannableBuilder);
    this.onHideHighlightClickListener = onHideHighlightClickListener;
  }

  public void renderHighLight(HighlightedShotModel highlightedShotModel, final ShotModel shotModel,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, OnUrlClickListener onUrlClickListener,
      OnOpenShotMenuListener onOpenShotMenuListener, OnReshootClickListener onReshootClickListener, Boolean isAdmin) {
    super.render(shotModel, shotClickListener, onShotLongClick, onLongClickListener,
        onTouchListener, onImageClickListener, onUrlClickListener, onOpenShotMenuListener, onReshootClickListener);
    setupSwipeLayout();
    setupHighlightedMenu();
    setupListeners(highlightedShotModel, shotClickListener, onShotLongClick,
        onOpenShotMenuListener);

    if (isAdmin) {
      dismissContainer.setBackgroundColor(
          dismissContainer.getResources().getColor(R.color.dissmiss));
    } else {
      dismissContainer.setBackgroundColor(
          dismissContainer.getResources().getColor(R.color.gray_50));
    }
  }

  private void setupHighlightedMenu() {
    openImageMenu.setVisibility(View.GONE);
    openMenuContainer.setVisibility(View.VISIBLE);
    openHighlightedMenu.setVisibility(View.VISIBLE);
  }

  private void setupSwipeLayout() {
    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
  }

  private void setupListeners(final HighlightedShotModel highlightedShotModel,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick,
      final OnOpenShotMenuListener onOpenShotMenuListener) {
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
    openHighlightedMenu.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onOpenShotMenuListener.openMenu(highlightedShotModel.getShotModel());
      }
    });
  }
}
