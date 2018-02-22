package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
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
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class CtaShotViewHolder extends ShotViewHolder {

  @BindView(R.id.shot_caption) TextView shotCaption;
  @BindView(R.id.check_in_button) TextView checkInButton;

  public CtaShotViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      AndroidTimeUtils timeUtils, ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    super(itemView, avatarClickListener, videoClickListener, onNiceShotListener, timeUtils,
        imageLoader, numberFormatUtil);
  }

  public void render(final ShotModel shotModel, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnReshootClickListener onReshootClickListener, OnUrlClickListener onUrlClickListener,
      OnOpenShotMenuListener onOpenShotMenuListener, final OnCtaClickListener onCtaClickListener) {

    super.render(shotModel, shotClickListener, onShotLongClick, onLongClickListener,
        onTouchListener, onImageClickListener, onReshootClickListener, onUrlClickListener,
        onOpenShotMenuListener);

    setupCaption(shotModel);
    setupButton(shotModel);
    checkInButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onCtaClickListener.onCtaClick(shotModel);
      }
    });
  }

  private void setupButton(ShotModel shotModel) {
    if (shotModel.getCtaButtonText() != null) {
      checkInButton.setText(shotModel.getCtaButtonText());
    }
  }

  private void setupCaption(ShotModel shotModel) {
    if (shotModel.getCtaCaption() != null) {
      shotCaption.setText(shotModel.getCtaCaption());
    }
  }
}
