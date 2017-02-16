package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.daimajia.swipe.SwipeLayout;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotTimelineViewHolder extends RecyclerView.ViewHolder {

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final OnOpenShotMenuListener onOpenShotMenuListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final NumberFormatUtil numberFormatUtil;

  @BindView(R.id.shot_avatar) ImageView avatar;
  @BindView(R.id.shot_user_name) TextView name;
  @BindView(R.id.verified_user) ImageView verifiedUser;
  @BindView(R.id.holder_or_contributor_user) ImageView holderOrContributor;
  @Nullable @BindView(R.id.shot_timestamp) TextView timestamp;
  @BindView(R.id.shot_text) ClickableTextView text;
  @BindView(R.id.shot_image_landscape) ProportionalImageView proportionalImageView;
  @BindView(R.id.default_image) ImageView defaultImage;
  @BindView(R.id.shot_video_frame) View videoFrame;
  @BindView(R.id.shot_video_title) TextView videoTitle;
  @BindView(R.id.shot_video_duration) TextView videoDuration;
  @BindView(R.id.shot_nice_button) ShineButton niceButton;
  @BindView(R.id.shot_nice_count) TextView niceCount;
  @BindView(R.id.shot_reply_count) TextView replyCount;
  @BindView(R.id.shot_container) View shotContainer;
  @BindView(R.id.shot_media_content) FrameLayout shotMediaContent;
  @BindView(R.id.open_menu) LinearLayout openImageMenu;
  @BindView(R.id.open_menu_container) FrameLayout openMenuContainer;
  @BindView(R.id.swipe) SwipeLayout swipeLayout;
  @Nullable @BindView(R.id.reshoot_container) FrameLayout reshootContainer;

  public int position;
  private View view;

  public ShotTimelineViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnOpenShotMenuListener onOpenShotMenuListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ImageLoader imageLoader, NumberFormatUtil numberFormatUtil,
      ShotTextSpannableBuilder shotTextSpannableBuilder) {
    super(view);
    this.numberFormatUtil = numberFormatUtil;
    ButterKnife.bind(this, view);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.imageLoader = imageLoader;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.view = view;
  }

  public void render(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnReshootClickListener onReshootClickListener, OnUrlClickListener onUrlClickListener,
      OnOpenShotMenuListener onOpenShotMenuListener) {
    bindUsername(shot);
    setupVerified(shot);
    setupIsHolderOrContributor(shot);
    bindComment(shot, onUrlClickListener);
    bindElapsedTime(shot);
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot, onLongClickListener, onTouchListener, onImageClickListener);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick, onReshootClickListener,
        onOpenShotMenuListener);
    setupSwipeLayout();
  }

  public void render(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnUrlClickListener onUrlClickListener, OnOpenShotMenuListener onOpenShotMenuListener) {
    bindUsername(shot);
    setupVerified(shot);
    setupIsHolderOrContributor(shot);
    bindComment(shot, onUrlClickListener);
    bindElapsedTime(shot);
    bindUserPhoto(shot);
    setupShotMediaContentVisibility(shot);
    bindImageInfo(shot, onLongClickListener, onTouchListener, onImageClickListener);
    bindVideoInfo(shot);
    bindNiceInfo(shot);
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick, null, onOpenShotMenuListener);
    setupSwipeLayout();
  }

  private void setupSwipeLayout() {
    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
  }

  private void setupListeners(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick,
      @Nullable final OnReshootClickListener reshootClickListener,
      final OnOpenShotMenuListener onOpenShotMenuListener) {
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
    if (reshootContainer != null) {
      reshootContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (reshootClickListener != null) {
            reshootClickListener.onReshootClick(shot);
            swipeLayout.close(true);
          }
        }
      });
    }
    if (openImageMenu != null) {
      openImageMenu.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onOpenShotMenuListener != null) {
            onOpenShotMenuListener.openMenu(shot);
          }
        }
      });
    }
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

  protected void bindComment(ShotModel item, OnUrlClickListener onUrlClickListener) {
    String comment = item.getComment();
    if (comment != null) {
      addShotComment(this, comment, onUrlClickListener);
      text.setVisibility(View.VISIBLE);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private void addShotComment(ShotTimelineViewHolder vh, CharSequence comment,
      OnUrlClickListener onUrlClickListener) {
    CharSequence spannedComment =
        shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
    vh.text.setText(spannedComment);
    vh.text.addLinks(onUrlClickListener);
  }

  private void bindUsername(ShotModel shot) {
    String usernameTitle = shot.getUsername();
    if (shot.isReply()) {
      name.setText(getReplyName(shot));
    } else {
      name.setText(usernameTitle);
    }
  }

  private void setupVerified(ShotModel shot) {
    if (shot.getParentShotId() == null && shot.isVerifiedUser()) {
      verifiedUser.setVisibility(View.VISIBLE);
    } else {
      verifiedUser.setVisibility(View.GONE);
    }
  }

  private void setupIsHolderOrContributor(ShotModel shot) {
    if (!shot.isVerifiedUser() && shot.getParentShotId() == null && shot.isHolderOrContributor()) {
      holderOrContributor.setVisibility(View.VISIBLE);
    } else {
      holderOrContributor.setVisibility(View.GONE);
    }
  }

  private String getReplyName(ShotModel shot) {
    return view.getContext()
        .getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
  }

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    if (timestamp != null) {
      this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
    }
  }

  private void bindUserPhoto(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onAvatarClick(shot.getIdUser(), v);
      }
    });
  }

  private void setupShotMediaContentVisibility(ShotModel shotModel) {
    if (shotModel.hasVideo() || shotModel.getImage().getImageUrl() != null) {
      openMenuContainer.setVisibility(View.VISIBLE);
      shotMediaContent.setVisibility(View.VISIBLE);
      openImageMenu.setVisibility(View.VISIBLE);
    } else {
      openMenuContainer.setVisibility(View.GONE);
      shotMediaContent.setVisibility(View.GONE);
      openImageMenu.setVisibility(View.GONE);
    }
  }

  private void bindImageInfo(final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    String imageUrl = shot.getImage().getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      if (isValidImageSizes(shot)) {
        setupProportionalImage(shot, onLongClickListener, onTouchListener, onImageClickListener,
            imageUrl);
      } else {
        proportionalImageView.setVisibility(View.GONE);
        setupImage(defaultImage, shot, onLongClickListener, onTouchListener, onImageClickListener,
            imageUrl);
      }
    } else {
      defaultImage.setVisibility(View.GONE);
      proportionalImageView.setVisibility(View.GONE);
    }
  }

  private void setupProportionalImage(ShotModel shot, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      String imageUrl) {
    defaultImage.setVisibility(View.GONE);
    proportionalImageView.setInitialHeight(shot.getImage().getImageHeight().intValue());
    proportionalImageView.setInitialWidth(shot.getImage().getImageWidth().intValue());
    setupImage(proportionalImageView, shot, onLongClickListener, onTouchListener,
        onImageClickListener, imageUrl);
  }

  private boolean isValidImageSizes(ShotModel shot) {
    return shot.getImage().getImageHeight() != null
        && shot.getImage().getImageHeight() != 0
        && shot.getImage().getImageWidth() != null
        && shot.getImage().getImageWidth() != 0;
  }

  private void setupImage(ImageView imageView, final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      final OnImageClickListener onImageClickListener, String imageUrl) {
    imageView.setVisibility(View.VISIBLE);
    imageLoader.loadTimelineImage(imageUrl, imageView);
    imageView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        onLongClickListener.onImageLongClick(shot);
        return true;
      }
    });
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onImageClickListener.onImageClick(view, shot);
      }
    });
    imageView.setOnTouchListener(onTouchListener);
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
    niceButton.setVisibility(View.VISIBLE);
    Integer nicesCount = shot.getNiceCount();
    if (nicesCount > 0) {
      setNiceCount(nicesCount);
    } else {
      this.niceCount.setVisibility(View.INVISIBLE);
    }

    niceButton.setChecked(shot.isMarkedAsNice());
    niceButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (shot.isMarkedAsNice()) {
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
}
