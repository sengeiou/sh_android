package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.widgets.BaseMessageTextView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class MessageViewHolder extends RecyclerView.ViewHolder {

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;

  @BindView(R.id.message_avatar) ImageView avatar;
  @BindView(R.id.message_user_name) TextView name;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @Nullable @BindView(R.id.message_timestamp) TextView timestamp;
  @BindView(R.id.message_text) BaseMessageTextView text;
  @BindView(R.id.message_image_landscape) ProportionalImageView proportionalImageView;
  @BindView(R.id.default_image) ImageView defaultImage;
  @BindView(R.id.message_video_frame) View videoFrame;
  @BindView(R.id.message_video_title) TextView videoTitle;
  @BindView(R.id.message_video_duration) TextView videoDuration;
  @BindView(R.id.message_media_content) FrameLayout messageMediaContent;

  public int position;
  private View view;

  public MessageViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnUsernameClickListener onUsernameClickListener,
      AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      ShotTextSpannableBuilder messageTextSpannableBuilder) {
    super(view);
    ButterKnife.bind(this, view);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.imageLoader = imageLoader;
    this.shotTextSpannableBuilder = messageTextSpannableBuilder;
    this.view = view;
  }

  public void render(final PrivateMessageModel message, OnImageClickListener onImageClickListener,
      OnUrlClickListener onUrlClickListener) {
    bindUsername(message);
    setupVerified(message);
    bindComment(message, onUrlClickListener);
    bindElapsedTime(message);
    bindUserPhoto(message);
    setupmessageMediaContentVisibility(message);
    bindImageInfo(message, onImageClickListener);
    bindVideoInfo(message);
  }

  protected void bindComment(PrivateMessageModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    if (comment != null) {
      addmessageComment(this, comment, onUrlClickListener, item);
      text.setVisibility(View.VISIBLE);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private void addmessageComment(MessageViewHolder vh, CharSequence comment,
      OnUrlClickListener onUrlClickListener, PrivateMessageModel item) {
    CharSequence spannedComment =
        shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
    vh.text.setText(spannedComment);
    vh.text.setBaseMessageModel(item);
    vh.text.addLinks();
  }

  private void bindUsername(PrivateMessageModel message) {
    String usernameTitle = message.getUsername();
    name.setText(usernameTitle);
  }

  private void setupVerified(PrivateMessageModel message) {
    if (message.isVerifiedUser()) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void bindElapsedTime(PrivateMessageModel message) {
    long messageTimestamp = message.getBirth().getTime();
    if (timestamp != null) {
      this.timestamp.setText(timeUtils.getHourWithDate(messageTimestamp));
    }
  }

  private void bindUserPhoto(final PrivateMessageModel message) {
    imageLoader.loadProfilePhoto(message.getAvatar(), avatar);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onAvatarClick(message.getIdUser(), v);
      }
    });
  }

  private void setupmessageMediaContentVisibility(PrivateMessageModel privateMessageModel) {
    if (privateMessageModel.hasVideo() || privateMessageModel.getImage().getImageUrl() != null) {
      messageMediaContent.setVisibility(View.VISIBLE);
    } else {
      messageMediaContent.setVisibility(View.GONE);
    }
  }

  private void bindImageInfo(final PrivateMessageModel message,
      OnImageClickListener onImageClickListener) {
    String imageUrl = message.getImage().getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      if (isValidImageSizes(message)) {
        setupProportionalImage(message, onImageClickListener, imageUrl);
      } else {
        proportionalImageView.setVisibility(View.GONE);
        setupImage(defaultImage, message, onImageClickListener, imageUrl);
      }
    } else {
      defaultImage.setVisibility(View.GONE);
      proportionalImageView.setVisibility(View.GONE);
    }
  }

  private void setupProportionalImage(PrivateMessageModel message,
      OnImageClickListener onImageClickListener, String imageUrl) {
    defaultImage.setVisibility(View.GONE);
    proportionalImageView.setInitialHeight(message.getImage().getImageHeight().intValue());
    proportionalImageView.setInitialWidth(message.getImage().getImageWidth().intValue());
    setupImage(proportionalImageView, message, onImageClickListener, imageUrl);
  }

  private boolean isValidImageSizes(PrivateMessageModel message) {
    return message.getImage().getImageHeight() != null
        && message.getImage().getImageHeight() != 0
        && message.getImage().getImageWidth() != null
        && message.getImage().getImageWidth() != 0;
  }

  private void setupImage(ImageView imageView, final PrivateMessageModel message,
      final OnImageClickListener onImageClickListener, String imageUrl) {
    imageView.setVisibility(View.VISIBLE);
    imageLoader.loadTimelineImage(imageUrl, imageView);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onImageClickListener.onImageClick(view, message);
      }
    });
  }

  private void bindVideoInfo(final PrivateMessageModel message) {
    if (message.hasVideo()) {
      videoFrame.setVisibility(View.VISIBLE);
      videoTitle.setText(message.getVideoTitle());
      videoDuration.setText(message.getVideoDuration());
      videoFrame.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          videoClickListener.onVideoClick(message.getVideoUrl());
        }
      });
    } else {
      videoFrame.setVisibility(View.GONE);
      videoFrame.setOnClickListener(null);
    }
  }
}