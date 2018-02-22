package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.daimajia.swipe.SwipeLayout;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDeleteHighlightedItemClick;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class FixedShotViewHolder extends ShotViewHolder {

  protected final OnDeleteHighlightedItemClick onHideHighlightClickListener;

  @BindView(R.id.hide_highlighted) TextView hideHighlighted;
  @BindView(R.id.shot_container) View shotContainer;
  @BindView(R.id.dismiss_container) FrameLayout dismissContainer;
  @BindView(R.id.swipe) SwipeLayout swipeLayout;
  @BindView(R.id.open_menu) ImageView openHighlightedMenu;
  @BindView(R.id.open_menu_container) FrameLayout openMenuContainer;

  public FixedShotViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      AndroidTimeUtils timeUtils, ImageLoader imageLoader, NumberFormatUtil numberFormatUtil,
      OnDeleteHighlightedItemClick onHideHighlightClickListener) {
    super(itemView, avatarClickListener, videoClickListener, onNiceShotListener, timeUtils,
        imageLoader, numberFormatUtil);
    this.onHideHighlightClickListener = onHideHighlightClickListener;
  }

  public void renderFixedShot(ShotModel shotModel, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnUrlClickListener onUrlClickListener,
      OnOpenShotMenuListener onOpenShotMenuListener, OnReshootClickListener onReshootClickListener,
      Boolean isAdmin) {
    super.render(shotModel, shotClickListener, onShotLongClick, onLongClickListener,
        onTouchListener, onImageClickListener, onReshootClickListener, onUrlClickListener,
        onOpenShotMenuListener);
    setupSwipeLayout();
    setupHighlightedMenu();
    setupListeners(shotModel, shotClickListener, onShotLongClick, onOpenShotMenuListener);

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

  private void setupListeners(final ShotModel shotModel,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick,
      final OnOpenShotMenuListener onOpenShotMenuListener) {
    shotContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        shotClickListener.onClick(shotModel);
      }
    });
    shotContainer.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        onShotLongClick.onShotLongClick(shotModel);
        return false;
      }
    });
    hideHighlighted.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onHideHighlightClickListener.onHideClick(shotModel);
      }
    });
    openHighlightedMenu.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onOpenShotMenuListener.openMenu(shotModel);
      }
    });
  }
}
