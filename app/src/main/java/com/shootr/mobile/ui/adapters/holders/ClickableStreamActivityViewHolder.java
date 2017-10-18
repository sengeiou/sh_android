package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
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

public abstract class ClickableStreamActivityViewHolder extends GenericActivityViewHolder {

  private final OnStreamTitleClickListener onStreamTitleClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;

  public ClickableStreamActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.onStreamTitleClickListener = onStreamTitleClickListener;
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override protected void renderText(ActivityModel activity) {
    text.setText(getFormattedUserName(activity));
    text.setMovementMethod(new LinkMovementMethod());
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(getCommentPattern())
        .build();
  }

  @NonNull protected abstract String getCommentPattern();
}
