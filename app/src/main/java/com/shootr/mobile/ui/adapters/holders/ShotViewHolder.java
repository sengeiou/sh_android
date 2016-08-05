package com.shootr.mobile.ui.adapters.holders;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableEmojiconTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotViewHolder {

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
    @Bind(R.id.shot_text) ClickableEmojiconTextView text;
    @Bind(R.id.shot_text_stream_title) ClickableEmojiconTextView streamTitle;
    @Bind(R.id.shot_image_landscape) ImageView imageLandscape;
    @Bind(R.id.shot_image_portrait) ImageView imagePortrait;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
    @Bind(R.id.shot_nice_count) TextView niceCount;
    @Bind(R.id.shot_hide_button_container) View hideContainer;
    @Bind(R.id.shot_reply_count) TextView replyCount;
    @Bind(R.id.shot_reply_button) ImageView darkReplyButton;
    @Bind(R.id.shot_reply_button_no_replies) ImageView lightReplyButton;

    @BindColor(R.color.short_title_color) int titleColor;

    public int position;
    private View view;
    private Boolean isCurrentUser;

    public ShotViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnReplyShotListener onReplyShotListener, OnHideClickListener onHideClickListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      ShotTextSpannableBuilder shotTextSpannableBuilder, Boolean isCurrentUser) {
        ButterKnife.bind(this, view);
        this.avatarClickListener = avatarClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
        this.onReplyShotListener = onReplyShotListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.imageLoader = imageLoader;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.view = view;
        this.onHideClickListener = onHideClickListener;
        this.isCurrentUser = isCurrentUser;
    }

    public void render(ShotModel shot, boolean shouldShowTitle) {
        bindUsername(shot);
        bindComment(shot, shouldShowTitle);
        bindElapsedTime(shot);
        bindUserPhoto(shot);
        bindImageInfo(shot);
        bindVideoInfo(shot);
        if (isCurrentUser) {
            bindHideButton(shot);
        } else {
            bindNiceInfo(shot);
        }
        bindReplyCount(shot);
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
            replyCount.setVisibility(View.GONE);
            darkReplyButton.setVisibility(View.GONE);
            lightReplyButton.setVisibility(View.VISIBLE);
            lightReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onReplyShotListener.reply(shot);
                }
            });
        }
    }

    private void bindHideButton(final ShotModel shot) {
        hideContainer.setVisibility(View.VISIBLE);
        niceButton.setVisibility(View.GONE);
        hideContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onHideClickListener.onHideClick(shot.getIdShot());
            }
        });
    }

    protected void bindComment(ShotModel item, boolean shouldShowTitle) {
        String comment = item.getComment();
        String title = null;
        if (shouldShowTitle && item.getStreamTitle() != null) {
            title = item.getStreamTitle();
        }
        if (comment == null && title == null) {
            text.setVisibility(View.GONE);
            streamTitle.setVisibility(View.GONE);
        }
        if (comment != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(comment);
            text.setVisibility(View.VISIBLE);
            addShotComment(this, spannableStringBuilder);
        } else {
            text.setVisibility(View.GONE);
        }
        if (title != null) {
            streamTitle.setVisibility(View.VISIBLE);
            streamTitle.setText(title);
        }
    }

    private void addShotComment(ShotViewHolder vh, CharSequence comment) {
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

    private void bindImageInfo(final ShotModel shot) {
        String imageUrl = shot.getImage().getImageUrl();
        Long imageWidth = shot.getImage().getImageWidth();
        Long imageHeight = shot.getImage().getImageHeight();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            handleImage(imageUrl,
                imageWidth, imageHeight);
        } else {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.GONE);
        }
    }

    private void handleImage(String imageUrl, Long imageWidth, Long imageHeight) {
        if (isImageValid(imageWidth, imageHeight)) {
            setImageLayout(imageUrl,
                imageWidth, imageHeight);
        } else {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.VISIBLE);
            setupImage(imageLandscape, imageUrl);
        }
    }

    private void setImageLayout(String imageUrl, Long imageWidth, Long imageHeight) {
        if (imageWidth > imageHeight) {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.VISIBLE);
            setupImage(imageLandscape, imageUrl);
        } else {
            imageLandscape.setVisibility(View.GONE);
            imagePortrait.setVisibility(View.VISIBLE);
            setupImage(imagePortrait, imageUrl);
        }
    }

    private void setupImage(ImageView imageView, String imageUrl) {
        imageLoader.loadTimelineImage(imageUrl, imageView);
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
        hideContainer.setVisibility(View.GONE);
        niceButton.setVisibility(View.VISIBLE);

        Integer nicesCount = shot.getNiceCount();
        if (nicesCount > 0) {
            setNiceCount(nicesCount);
        } else {
            this.niceCount.setVisibility(View.GONE);
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

}
