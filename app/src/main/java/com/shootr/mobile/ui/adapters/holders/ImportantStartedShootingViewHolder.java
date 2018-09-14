package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.StreamTitleBoldSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class ImportantStartedShootingViewHolder extends ShotActivityViewHolder {

  @BindString(R.string.started_shooting_activity_text_pattern) String
      startedShootingPattern;
  @BindString(R.string.important_started_shooting_activity_text_pattern) String
      startedShootingPatternWithComment;
  @BindString(R.string.admin) String
      adminResource;
  private final AndroidTimeUtils androidTimeUtils;

  public ImportantStartedShootingViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnShotClick onShotClick, OnStreamTitleClickListener onStreamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClick,
        onStreamTitleClickListener);
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override protected CharSequence getTitle(ActivityModel activity) {

    StreamTitleBoldSpan streamTitleSpan =
        new StreamTitleBoldSpan(activity.getIdStream(), activity.getStreamTitle()) {
          @Override
          public void onStreamClick(String streamId, String streamTitle) {
            onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle);
          }
        };
    return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
        .append(adminResource)
        .popSpan()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername())
        .popSpan()
        .append(getActivitySimpleComment(activity))
        .append(" ")
        .pushSpan(streamTitleSpan)
        .append(verifiedStream(activity.getStreamTitle(), activity.isVerified()))
        .popSpan()
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  @Override protected String getActivitySimpleComment(ActivityModel activity) {
    return startedShootingPattern;
  }

}
