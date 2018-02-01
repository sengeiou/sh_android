package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import butterknife.BindView;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.activity.ActivityType;
import com.shootr.mobile.ui.adapters.listeners.ActivityFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.ui.widgets.StreamTitleBoldSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public abstract class ClickableStreamActivityViewHolder extends GenericActivityViewHolder {

  private final OnStreamTitleClickListener onStreamTitleClickListener;
  private final ActivityFavoriteClickListener activityFavoriteClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindView(R.id.follow_button) FollowButton followButton;
  @BindColor(R.color.gray_60) int gray_60;

  public ClickableStreamActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener,
      ActivityFavoriteClickListener activityFavoriteClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.onStreamTitleClickListener = onStreamTitleClickListener;
    this.androidTimeUtils = androidTimeUtils;
    this.activityFavoriteClickListener = activityFavoriteClickListener;
  }

  @Override protected void renderText(ActivityModel activity) {
    title.setText(getFormattedUserName(activity));
    title.setMovementMethod(new LinkMovementMethod());
    text.setVisibility(View.GONE);
  }

  @Override protected void renderFollowButton(final ActivityModel activity) {
    if (!activity.getType().equals(ActivityType.CHECKIN)) {
      followButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (activity.isFavorite()) {
            activityFavoriteClickListener.onRemoveFavoriteClick(activity.getIdStream());
            activity.setFavorite(false);
            followButton.setFollowing(false);
          } else {
            activityFavoriteClickListener.onFavoriteClick(activity.getIdStream(),
                activity.getStreamTitle(), activity.isStrategic());
            activity.setFavorite(true);
            followButton.setFollowing(true);
          }
        }
      });
      if (!activity.isFavorite()) {
        followButton.setVisibility(View.VISIBLE);
        followButton.setFollowing(false);
      }
    }
  }

  @Override protected void renderTargetAvatar(final ActivityModel activity) {
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
    StreamTitleBoldSpan streamTitleSpan =
        new StreamTitleBoldSpan(activity.getIdStream(), activity.getStreamTitle(),
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
        .pushSpan(streamTitleSpan)
        .append(verifiedStream(activity.getStreamTitle(), activity.isVerified()))
        .popSpan()
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  @NonNull protected abstract String getCommentPattern();
}
