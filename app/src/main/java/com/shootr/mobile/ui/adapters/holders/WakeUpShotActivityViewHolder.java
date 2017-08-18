package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class WakeUpShotActivityViewHolder extends GenericActivityViewHolder {

  private final OnStreamTitleClickListener streamTitleClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;

  public WakeUpShotActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnStreamTitleClickListener streamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.streamTitleClickListener = streamTitleClickListener;
    this.androidTimeUtils = androidTimeUtils;
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
    text.setText(getFormattedUserName(activity));
    text.setMovementMethod(new LinkMovementMethod());
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(" ")
        .append(activity.getShot().getComment())
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }
}
