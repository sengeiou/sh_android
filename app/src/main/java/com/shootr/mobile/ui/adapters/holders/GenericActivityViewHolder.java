package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityType;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.BaseMessageTextView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class GenericActivityViewHolder extends RecyclerView.ViewHolder {

  protected final ImageLoader imageLoader;
  private final AndroidTimeUtils androidTimeUtils;
  private final OnAvatarClickListener onAvatarClickListener;
  @BindColor(R.color.gray_60) int gray_60;

  @BindView(R.id.activity_avatar) AvatarView avatar;
  @BindView(R.id.activity_target_avatar) AvatarView targetAvatar;
  @BindView(R.id.activity_text) BaseMessageTextView text;
  @BindView(R.id.activity_title) TextView title;
  @BindView(R.id.shot_image) ImageView image;
  @BindView(R.id.embed_shot_comment) BaseMessageTextView embedShotComment;
  @BindView(R.id.embed_user) TextView embedUsername;
  @BindView(R.id.embed_shot_image) ImageView embedShotImage;
  @BindView(R.id.embed_card) CardView embedCard;
  @BindView(R.id.info_container) LinearLayout infoContainer;
  //@BindView(R.id.stream_name) TextView streamName;
  //@BindView(R.id.stream_verified) ImageView verified;

  public GenericActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener) {
    super(view);
    this.imageLoader = imageLoader;
    this.androidTimeUtils = androidTimeUtils;
    this.onAvatarClickListener = onAvatarClickListener;
    ButterKnife.bind(this, view);
  }

  public void render(final ActivityModel activity) {
    renderTitle(activity);
    renderText(activity);
    renderAvatar(activity);
    rendetTargetAvatar(activity);
    renderImage(activity);
    renderVerified(activity);
  }

  protected void renderTitle(final ActivityModel activity) {
    if (activity.getComment() != null) {
      title.setText(getFormattedUserName(activity));
      if (activity.getType().equals(ActivityType.PROFILE_UPDATED)) {
        infoContainer.setVisibility(View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            onAvatarClickListener.onAvatarClick(activity.getIdUser(), avatar);
          }
        });
      }
    }
  }

  protected void rendetTargetAvatar(final ActivityModel activity) {
    if (activity.getTargetName() != null) {
      imageLoader.loadProfilePhoto(activity.getTargetUserPhoto(), targetAvatar, activity.getTargetName());
      targetAvatar.setVisibility(View.VISIBLE);
      targetAvatar.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onAvatarClickListener.onAvatarClick(activity.getIdTargetUser(), targetAvatar);
        }
      });
    } else {
      targetAvatar.setVisibility(View.GONE);
    }
  }

  protected void renderText(ActivityModel activity) {
    /* no-op */
  }

  protected void renderVerified(ActivityModel activity) {
    if (activity.isVerified()) {
      //verified.setVisibility(View.VISIBLE);
    } else {
      //verified.setVisibility(View.GONE);
    }
  }

  protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername())
        .popSpan()
        .append(" ")
        .append(activity.getComment().toLowerCase())
        .build();
  }

  protected void renderAvatar(final ActivityModel activity) {
    imageLoader.loadProfilePhoto(activity.getUserPhoto(), avatar, activity.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onAvatarClickListener.onAvatarClick(activity.getIdUser(), avatar);
      }
    });
  }

  protected void renderImage(ActivityModel activity) {
    image.setVisibility(View.GONE);
  }

  protected Context getContext() {
    return itemView.getContext();
  }

}
