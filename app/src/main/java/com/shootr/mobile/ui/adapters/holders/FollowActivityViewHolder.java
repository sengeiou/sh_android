package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.ActivityFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.Truss;

public class FollowActivityViewHolder extends GenericActivityViewHolder {

  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final OnUsernameClickListener onUsernameClickListener;
  private final ActivityFollowUnfollowListener onFollowUnfollowListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;

  private String currentUserId;

  public FollowActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener, OnUsernameClickListener onUsernameClickListener,
      ActivityFollowUnfollowListener onFollowUnfollowListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.onUsernameClickListener = onUsernameClickListener;
    this.onFollowUnfollowListener = onFollowUnfollowListener;
    this.androidTimeUtils = androidTimeUtils;
  }

  public void setCurrentUserId(String currentUserId) {
    this.currentUserId = currentUserId;
  }

  @Override protected void renderText(ActivityModel activity) {
    try {
      if (currentUserId != null) {
        title.setText(getFormattedUserName(activity));
        title.setVisibility(View.VISIBLE);
      }
    } catch (Exception e) {
      /* no-op */
    }
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss() //
        .pushSpan(new StyleSpan(Typeface.BOLD)) //
        .append(activity.getUsername()).popSpan()//
        .append(" ").append(formatActivityComment(activity, currentUserId)) //
        .build();
  }

  protected CharSequence formatActivityComment(final ActivityModel activity, String currentUserId) {
    if (activity.getIdTargetUser() != null && activity.getIdTargetUser().equals(currentUserId)) {
      activity.setComment(itemView.getContext().getString(R.string.activity_started_following_you));
    } else {
      activity.setComment(
          activity.getComment().substring(0, 1).toLowerCase() + activity.getComment().substring(1));
    }
    return shotTextSpannableBuilder.formatWithUsernameSpans(activity.getComment(),
        onUsernameClickListener);
  }
}
