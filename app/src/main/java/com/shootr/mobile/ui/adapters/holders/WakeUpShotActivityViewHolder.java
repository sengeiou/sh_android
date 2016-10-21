package com.shootr.mobile.ui.adapters.holders;

import android.text.method.LinkMovementMethod;
import android.view.View;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class WakeUpShotActivityViewHolder extends GenericActivityViewHolder {

  private final OnStreamTitleClickListener streamTitleClickListener;

  public WakeUpShotActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnStreamTitleClickListener streamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.streamTitleClickListener = streamTitleClickListener;
  }

  @Override public void render(final ActivityModel activity) {
    super.render(activity);
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        streamTitleClickListener.onStreamTitleClick(activity.getIdStream(),
            activity.getStreamTitle(), activity.getIdStreamAuthor());
      }
    });
  }
  @Override protected void renderText(ActivityModel activity) {
    text.setText(activity.getShot().getComment());
    text.setMovementMethod(new LinkMovementMethod());
  }

}
