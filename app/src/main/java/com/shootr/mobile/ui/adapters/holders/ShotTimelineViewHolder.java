package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotTimelineViewHolder extends RecyclerView.ViewHolder {

  private static final int LONG_COMMENT_THRESHOLD = 20;

  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnReplyShotListener onReplyShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ImageLoader imageLoader;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final OnHideClickListener onHideClickListener;

  @Bind(R.id.shot_avatar) ImageView avatar;
  @Bind(R.id.shot_user_name) TextView name;
  @Bind(R.id.shot_timestamp) TextView timestamp;
  @Bind(R.id.shot_text) ClickableTextView text;
  @Bind(R.id.shot_image_landscape) ImageView imageLandscape;
  @Bind(R.id.shot_image_portrait) ImageView imagePortrait;
  @Bind(R.id.shot_video_frame) View videoFrame;
  @Bind(R.id.shot_video_title) TextView videoTitle;
  @Bind(R.id.shot_video_duration) TextView videoDuration;
  @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
  @Bind(R.id.shot_nice_count) TextView niceCount;
  @Bind(R.id.nices_container) View niceContainer;
  @Bind(R.id.shot_reply_count) TextView replyCount;
  @Bind(R.id.shot_reply_button) ImageView darkReplyButton;
  @Bind(R.id.shot_reply_button_no_replies) ImageView lightReplyButton;

  @BindDimen(R.dimen.nice_button_margin_top_normal) int niceMarginNormal;

  @BindColor(R.color.short_title_color) int titleColor;

  public int position;
  private View view;
  private Boolean isCurrentUser;

  public ShotTimelineViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnReplyShotListener onReplyShotListener, OnHideClickListener onHideClickListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ImageLoader imageLoader, ShotTextSpannableBuilder shotTextSpannableBuilder,
      Boolean isCurrentUser) {
    super(view);
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onReplyShotListener = onReplyShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.imageLoader = imageLoader;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    ButterKnife.bind(this, view);
    this.view = view;
    this.onHideClickListener = onHideClickListener;
    this.isCurrentUser = isCurrentUser;
  }

  public void render(final ShotModel shot, boolean shouldShowTitle,
      final ShotClickListener shotClickListener, final OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    bindUsername(shot);
    bindComment(shot, shouldShowTitle);
    bindElapsedTime(shot);
    bindUserPhoto(shot);
    bindImageInfo(shot, onLongClickListener, onTouchListener, onImageClickListener);
    bindVideoInfo(shot);
    if (isCurrentUser) {
      //TODO
    } else {
      bindNiceInfo(shot);
    }
    bindReplyCount(shot);
    setupListeners(shot, shotClickListener, onShotLongClick);
  }

  private void setupListeners(final ShotModel shot, final ShotClickListener shotClickListener,
      final OnShotLongClick onShotLongClick) {
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        shotClickListener.onClick(shot);
      }
    });
    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(shot);
        return false;
      }
    });
  }

  private void bindReplyCount(final ShotModel shot) {
    Long replies = shot.getReplyCount();
    if (replies > 0L) {
      replyCount.setVisibility(View.VISIBLE);
      replyCount.setText(String.valueOf(replies));
      darkReplyButton.setVisibility(View.VISIBLE);
      lightReplyButton.setVisibility(View.GONE);
      darkReplyButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onReplyShotListener.reply(shot);
        }
      });
      replyCount.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onReplyShotListener.reply(shot);
        }
      });
    } else {
      replyCount.setVisibility(View.INVISIBLE);
      darkReplyButton.setVisibility(View.GONE);
      lightReplyButton.setVisibility(View.VISIBLE);
      lightReplyButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onReplyShotListener.reply(shot);
        }
      });
    }
  }

  protected void bindComment(ShotModel item, boolean shouldShowTitle) {
    String comment = item.getComment();
    String title = null;
    if (shouldShowTitle && item.getStreamTitle() != null) {
      title = item.getStreamTitle();
    }

    SpannableStringBuilder commentWithTitle = buildCommentTextWithTitle(comment, title);
    if (commentWithTitle != null) {
      addShotComment(this, commentWithTitle);
      text.setVisibility(View.VISIBLE);
    } else {
      text.setVisibility(View.GONE);
    }
  }

  private @Nullable SpannableStringBuilder buildCommentTextWithTitle(@Nullable String comment,
      @Nullable String title) {
    SpannableStringBuilder builder = new SpannableStringBuilder();
    if (comment == null && title == null) {
      return null;
    }
    if (comment != null) {
      builder.append(comment);
    }

    if (comment != null && title != null) {
      builder.append(" ");
    }
    if (title != null) {
      builder.append(formatAditionalInfo(title));
    }
    return builder;
  }

  private SpannableString formatAditionalInfo(String title) {
    ForegroundColorSpan span = new ForegroundColorSpan(titleColor);

    SpannableString titleSpan = new SpannableString(title);
    titleSpan.setSpan(span, 0, titleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return titleSpan;
  }

  private void addShotComment(ShotTimelineViewHolder vh, CharSequence comment) {
    CharSequence spannedComment =
        shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
    vh.text.setText(spannedComment);
    vh.text.addLinks();
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

  private void bindElapsedTime(ShotModel shot) {
    long shotTimestamp = shot.getBirth().getTime();
    this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
  }

  private void bindUserPhoto(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getPhoto(), avatar);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        avatarClickListener.onAvatarClick(shot.getIdUser(), v);
      }
    });
  }

  private void bindImageInfo(final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    String imageUrl = shot.getImage().getImageUrl();
    Long imageWidth = shot.getImage().getImageWidth();
    Long imageHeight = shot.getImage().getImageHeight();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      handleImage(shot, onLongClickListener, onTouchListener, onImageClickListener, imageUrl,
          imageWidth, imageHeight);
    } else {
      imagePortrait.setVisibility(View.GONE);
      imageLandscape.setVisibility(View.GONE);
    }
  }

  private void handleImage(final ShotModel shot, final OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      String imageUrl, Long imageWidth, Long imageHeight) {
    if (isImageValid(imageWidth, imageHeight)) {
      setImageLayout(shot, onLongClickListener, onTouchListener, onImageClickListener, imageUrl,
          imageWidth, imageHeight);
    } else {
      imagePortrait.setVisibility(View.GONE);
      imageLandscape.setVisibility(View.VISIBLE);
      setupImage(imageLandscape, shot, onLongClickListener, onTouchListener, onImageClickListener,
          imageUrl);
    }
  }

  private void setImageLayout(final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, String imageUrl, Long imageWidth,
      Long imageHeight) {
    if (imageWidth > imageHeight) {
      imagePortrait.setVisibility(View.GONE);
      imageLandscape.setVisibility(View.VISIBLE);
      setupImage(imageLandscape, shot, onLongClickListener, onTouchListener, onImageClickListener,
          imageUrl);
    } else {
      imageLandscape.setVisibility(View.GONE);
      imagePortrait.setVisibility(View.VISIBLE);
      setupImage(imagePortrait, shot, onLongClickListener, onTouchListener, onImageClickListener,
          imageUrl);
    }
  }

  private void setupImage(ImageView imageView, final ShotModel shot,
      final OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      final OnImageClickListener onImageClickListener, String imageUrl) {
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

  private boolean isImageValid(Long imageWidth, Long imageHeight) {
    return imageWidth != null && imageWidth != 0 && imageHeight != null && imageHeight != 0;
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
    boolean moveNiceButtonUp = !hasLongComment(shot) && !hasImage(shot);
    int marginTop = moveNiceButtonUp ? niceMarginNormal : niceMarginNormal;

    niceButton.setVisibility(View.VISIBLE);
    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) niceButton.getLayoutParams();
    lp.setMargins(0, marginTop, 0, 0);

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
          onNiceShotListener.markNice(shot.getIdShot());
        }
      }
    });
  }

  public void setNiceCount(Integer niceCount) {
    this.niceCount.setVisibility(View.VISIBLE);
    this.niceCount.setText(String.valueOf(niceCount));
  }

  private boolean hasLongComment(ShotModel shot) {
    return shot.getComment() != null && shot.getComment().length() > LONG_COMMENT_THRESHOLD;
  }

  private boolean hasImage(ShotModel shot) {
    return shot.getImage() != null;
  }
}