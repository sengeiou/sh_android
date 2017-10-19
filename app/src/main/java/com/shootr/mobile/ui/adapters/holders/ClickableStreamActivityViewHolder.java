package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.StreamTitleSpan;
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
    title.setText(getFormattedUserName(activity));
    title.setMovementMethod(new LinkMovementMethod());
    text.setVisibility(View.GONE);
  }

  @Override protected void rendetTargetAvatar(final ActivityModel activity) {
    if (activity.getStreamTitle() != null) {
      imageLoader.loadProfilePhoto(activity.getStreamPhoto(), targetAvatar,
          activity.getStreamTitle());
      targetAvatar.setVisibility(View.VISIBLE);
      targetAvatar.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onStreamTitleClickListener.onStreamTitleClick(activity.getIdStream(),
              activity.getStreamTitle(), activity.getIdStreamAuthor());
        }
      });
    } else {
      targetAvatar.setVisibility(View.GONE);
    }
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    StreamTitleSpan streamTitleSpan =
        new StreamTitleSpan(activity.getIdStream(), activity.getStreamTitle(),
            activity.getIdStreamAuthor()) {
          @Override
          public void onStreamClick(String streamId, String streamTitle, String idAuthor) {
            onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle, idAuthor);
          }
        };

    return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername())
        .popSpan()
        .append(getCommentPattern())
        .append(" ")
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .pushSpan(streamTitleSpan)
        .append(activity.getStreamTitle())
        .append(verifiedStream(activity.isVerified()))
        .popSpan()
        .build();
  }

  @NonNull protected abstract String getCommentPattern();
}
