package com.shootr.mobile.ui.adapters.holders;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotViewHolder {

    private final OnAvatarClickListener avatarClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnHideClickListener onHideClickListener;
    private final NumberFormatUtil numberFormatUtil;

    @BindView(R.id.shot_avatar) ImageView avatar;
    @BindView(R.id.shot_user_name) TextView name;
    @BindView(R.id.shot_timestamp) TextView timestamp;
    @BindView(R.id.shot_text) ClickableTextView text;
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

    public int position;
    private View view;
    private Boolean isCurrentUser;

    public ShotViewHolder(View view, OnAvatarClickListener avatarClickListener,
        OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
        OnHideClickListener onHideClickListener, OnUsernameClickListener onUsernameClickListener,
        AndroidTimeUtils timeUtils, ImageLoader imageLoader,
        ShotTextSpannableBuilder shotTextSpannableBuilder, NumberFormatUtil numberFormatUtil,
        Boolean isCurrentUser) {
        this.numberFormatUtil = numberFormatUtil;
        ButterKnife.bind(this, view);
        this.avatarClickListener = avatarClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
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
        setupShotMediaContentVisibility(shot);
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
            replyCount.setText(replyCount.getResources()
                .getQuantityString(R.plurals.shot_replies_count_pattern, replies.intValue(),
                    numberFormatUtil.formatNumbers(replies)));
        } else {
            replyCount.setVisibility(View.GONE);
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

    private void setupProportionalImage(ShotModel shot,
        String imageUrl) {
        defaultImage.setVisibility(View.GONE);
        proportionalImageView.setInitialHeight(shot.getImage().getImageHeight().intValue());
        proportionalImageView.setInitialWidth(shot.getImage().getImageWidth().intValue());
        setupImage(proportionalImageView, imageUrl);
    }

    private boolean isValidImageSizes(ShotModel shot) {
        return shot.getImage().getImageHeight() != null && shot.getImage().getImageHeight() != 0
            && shot.getImage().getImageWidth() != null && shot.getImage().getImageWidth() != 0;
    }

    private void setupImage(ImageView imageView, String imageUrl) {
        imageView.setVisibility(View.VISIBLE);
        imageLoader.loadTimelineImage(imageUrl, imageView);
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
                    onNiceShotListener.markNice(shot);
                }
            }
        });
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount.setVisibility(View.VISIBLE);
        this.niceCount.setText(String.valueOf(niceCount));
    }

    private void setupShotMediaContentVisibility(ShotModel shotModel) {
        if (shotModel.hasVideo() || shotModel.getImage().getImageUrl() != null) {
            shotMediaContent.setVisibility(View.VISIBLE);
        } else {
            shotMediaContent.setVisibility(View.GONE);
        }
    }

}
