package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.daimajia.swipe.SwipeLayout;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.BaseMessageTextView;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class ProfileShotViewHolder extends RecyclerView.ViewHolder {

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.shot_user_name) TextView name;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @BindView(R.id.shot_timestamp) TextView timestamp;
  @BindView(R.id.shot_text) BaseMessageTextView text;
  @BindView(R.id.shot_text_stream_title) ClickableTextView streamTitle;
  @BindView(R.id.shot_image_landscape) ProportionalImageView proportionalImageView;
  @BindView(R.id.default_image) ImageView defaultImage;
  @BindView(R.id.shot_video_frame) View videoFrame;
  @BindView(R.id.shot_video_title) TextView videoTitle;
  @BindView(R.id.shot_video_duration) TextView videoDuration;
  @BindView(R.id.shot_nice_button) NiceButtonView niceButton;
  @BindView(R.id.shot_nice_count) TextView niceCount;
  @BindView(R.id.shot_hide_button_container) View hideContainer;
  @BindView(R.id.shot_reply_count) TextView replyCount;
  @BindView(R.id.shot_media_content) FrameLayout shotMediaContent;
  @BindView(R.id.swipe) SwipeLayout swipeLayout;
  @BindView(R.id.reshoot_container) FrameLayout reshootContainer;
  @BindView(R.id.reshoot) TextView reshootText;
  @BindView(R.id.shot_container) View shotContainer;

  @BindString(R.string.menu_share_shot_via_shootr) String reshoootResource;
  @BindString(R.string.undo_reshoot) String undoReshootResource;

  public int position;
  private View view;

  public ProfileShotViewHolder(View itemView, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      AndroidTimeUtils timeUtils, ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    super(itemView);
    this.view = itemView;
    ButterKnife.bind(this, view);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.timeUtils = timeUtils;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
  }

  public void render(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnReshootClickListener onReshootClickListener,
      OnUrlClickListener onUrlClickListener) {
    bindUsername(shot);
    setupVerified(shot);
    bindComment(shot, onUrlClickListener);
    bindElapsedTime(shot);
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick, onReshootClickListener);
    setupSwipeLayout();
  }

  private void bindUsername(ShotModel shot) {
    String usernameTitle = shot.getUsername();
    if (shot.isReply()) {
      name.setText(getReplyName(shot));
    } else {
      name.setText(usernameTitle);
    }
  }

  private String getReplyName(ShotModel shot) {
    return view.getContext()
        .getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
  }

  private void setupVerified(ShotModel shotModel) {
    if (shotModel.getParentShotId() == null && shotModel.isVerifiedUser()) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  protected void bindComment(ShotModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    String title = null;
    if (item.getStreamTitle() != null) {
      title = item.getStreamTitle();
    }
    if (comment == null && title == null) {
      text.setVisibility(View.GONE);
      streamTitle.setVisibility(View.GONE);
    }
    if (comment != null) {
      SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(comment);
      text.setVisibility(View.VISIBLE);
      addShotComment(this, spannableStringBuilder, item, onUrlClickListener);
    } else {
      text.setVisibility(View.GONE);
    }
    if (title != null) {
      streamTitle.setVisibility(View.VISIBLE);
      streamTitle.setText(title);
    }
  }

  private void addShotComment(ProfileShotViewHolder vh, CharSequence comment, ShotModel shotModel,
      OnUrlClickListener onUrlClickListener) {
    vh.text.setBaseMessageModel(shotModel);
    vh.text.setText(comment);
    vh.text.setOnUrlClickListener(onUrlClickListener);
    vh.text.addLinks();
  }

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
  }

  private void bindUserPhoto(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar, shot.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onAvatarClick(shot.getIdUser(), v);
      }
    });
  }

  private void bindImageInfo(final ShotModel shot) {
    String imageUrl = shot.getImage().getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      if (isValidImageSizes(shot)) {
        setupProportionalImage(shot, imageUrl);
      } else {
        proportionalImageView.setVisibility(View.GONE);
        setupImage(defaultImage, imageUrl);
      }
    } else {
      defaultImage.setVisibility(View.GONE);
      proportionalImageView.setVisibility(View.GONE);
    }
  }

  private void setupProportionalImage(ShotModel shot, String imageUrl) {
    defaultImage.setVisibility(View.GONE);
    proportionalImageView.setInitialHeight(shot.getImage().getImageHeight().intValue());
    proportionalImageView.setInitialWidth(shot.getImage().getImageWidth().intValue());
    setupImage(proportionalImageView, imageUrl);
  }

  private boolean isValidImageSizes(ShotModel shot) {
    return shot.getImage().getImageHeight() != null
        && shot.getImage().getImageHeight() != 0
        && shot.getImage().getImageWidth() != null
        && shot.getImage().getImageWidth() != 0;
  }

  private void setupImage(ImageView imageView, String imageUrl) {
    imageView.setVisibility(View.VISIBLE);
    imageLoader.loadTimelineImage(imageUrl, imageView);
  }

  private void setupShotMediaContentVisibility(ShotModel shotModel) {
    if (shotModel.hasVideo() || shotModel.getImage().getImageUrl() != null) {
      shotMediaContent.setVisibility(View.VISIBLE);
    } else {
      shotMediaContent.setVisibility(View.GONE);
    }
  }

  private void bindVideoInfo(final ShotModel shot) {
    if (shot.hasVideo()) {
      videoFrame.setVisibility(View.VISIBLE);
      videoTitle.setText(shot.getVideoTitle());
      videoDuration.setText(shot.getVideoDuration());
      videoFrame.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          videoClickListener.onVideoClick(shot.getVideoUrl());
        }
      });
    } else {
      videoFrame.setVisibility(View.GONE);
      videoFrame.setOnClickListener(null);
    }
  }

  private void bindNiceInfo(final ShotModel shot) {
    hideContainer.setVisibility(View.GONE);
    niceButton.setVisibility(View.VISIBLE);

    Integer nicesCount = shot.getNiceCount();
    if (nicesCount > 0) {
      setNiceCount(nicesCount);
    } else {
      this.niceCount.setVisibility(View.GONE);
    }

    niceButton.setChecked(shot.isNiced());
    niceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (shot.isNiced()) {
          onNiceShotListener.unmarkNice(shot.getIdShot());
        } else {
          onNiceShotListener.markNice(shot);
        }
      }
    });
  }

  public void setNiceCount(Integer niceCount) {
    this.niceCount.setVisibility(View.VISIBLE);
    this.niceCount.setText(String.valueOf(niceCount));
  }

  private void bindReplyCount(final ShotModel shot) {
    Long replies = shot.getReplyCount();
    if (replies > 0L) {
      replyCount.setVisibility(View.VISIBLE);
      replyCount.setText(replyCount.getResources()
          .getQuantityString(R.plurals.shot_replies_count_pattern, replies.intValue(),
              numberFormatUtil.formatNumbers(replies)));
    } else {
      replyCount.setVisibility(View.GONE);
    }
  }

  private void setupListeners(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick,
      @Nullable final OnReshootClickListener reshootClickListener) {
    shotContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        shotClickListener.onClick(shot);
      }
    });
    shotContainer.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(shot);
        return false;
      }
    });
    setupSwipe(shot, reshootClickListener);
  }

  private void setupSwipe(final ShotModel shot,
      @Nullable final OnReshootClickListener reshootClickListener) {
    if (reshootContainer != null) {
      reshootContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (reshootClickListener != null) {
            if (!shot.isReshooted()) {
              reshootClickListener.onReshootClick(shot);
            } else {
              reshootClickListener.onUndoReshootClick(shot);
            }
            swipeLayout.close(true);
          }
        }
      });

      if (shot.isReshooted()) {
        reshootContainer.setBackgroundColor(
            reshootContainer.getResources().getColor(R.color.gray_material));
        if (reshootText != null) {
          reshootText.setText(undoReshootResource);
        }
      } else {
        reshootContainer.setBackgroundColor(
            reshootContainer.getResources().getColor(R.color.reshoot));
        if (reshootText != null) {
          reshootText.setText(reshoootResource);
        }
      }
    }
  }

  private void setupSwipeLayout() {
    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
  }
}
