package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.AvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotDetailReplyHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_reply_container) RelativeLayout container;
    @BindView(R.id.shot_avatar) public ImageView avatar;
    @BindView(R.id.shot_user_name) public TextView name;
    @BindView(R.id.shot_timestamp) public TextView timestamp;
    @BindView(R.id.shot_text) public ClickableTextView text;
    @BindView(R.id.shot_image_landscape) ProportionalImageView proportionalImageView;
    @BindView(R.id.default_image) ImageView defaultImage;
    @BindView(R.id.shot_video_frame) View videoFrame;
    @BindView(R.id.shot_video_title) TextView videoTitle;
    @BindView(R.id.shot_video_duration) TextView videoDuration;
    @BindView(R.id.shot_nice_button) NiceButtonView niceButton;
    @BindView(R.id.shot_media_content) FrameLayout shotMediaContent;
    @BindView(R.id.shot_reply_count) TextView replyCount;

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener replyShotClickListener;
    private final NumberFormatUtil numberFormatUtil;

    public ShotDetailReplyHolder(View itemView, ShotTextSpannableBuilder shotTextSpannableBuilder,
        OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
        AvatarClickListener avatarClickListener, ShotClickListener imageClickListener,
        OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
        ShotClickListener replyShotClickListener, NumberFormatUtil numberFormatUtil) {
        super(itemView);
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
        this.replyShotClickListener = replyShotClickListener;
        this.numberFormatUtil = numberFormatUtil;
        ButterKnife.bind(this, itemView);
    }

    public void bindView(final ShotModel reply) {
        this.name.setText(reply.getUsername());
        setupComment(reply);
        setupBirthData(reply);
        setupShotMediaContentVisibility(reply);
        loadImage(reply);
        bindImageInfo(reply, imageClickListener);
        setupVideoListener(reply);
        setupNiceListener(reply);
        setupReplyClickListener(reply);
        bindReplyCount(reply);
    }

    private void setupBirthData(ShotModel reply) {
        long creationDate = reply.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));
    }

    private void setupComment(ShotModel reply) {
        String comment = reply.getComment();
        if (comment != null) {
            this.text.setVisibility(View.VISIBLE);
            CharSequence spannedComment =
              shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
            this.text.setText(spannedComment);
            this.text.addLinks();
        } else {
            this.text.setVisibility(View.GONE);
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

    private void loadImage(final ShotModel reply) {
        String photo = reply.getPhoto();
        imageLoader.loadProfilePhoto(photo, this.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(reply.getIdUser());
            }
        });
    }

    private void bindImageInfo(final ShotModel shot, ShotClickListener onImageClickListener) {
        String imageUrl = shot.getImage().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (isValidImageSizes(shot)) {
                setupProportionalImageView(shot, onImageClickListener, imageUrl);
            } else {
                proportionalImageView.setVisibility(View.GONE);
                setupImage(defaultImage, shot, onImageClickListener, imageUrl);
            }
        } else {
            defaultImage.setVisibility(View.GONE);
            proportionalImageView.setVisibility(View.GONE);
        }
    }

    private void setupProportionalImageView(ShotModel shot, ShotClickListener onImageClickListener,
        String imageUrl) {
        defaultImage.setVisibility(View.GONE);
        proportionalImageView.setInitialHeight(shot.getImage().getImageHeight().intValue());
        proportionalImageView.setInitialWidth(shot.getImage().getImageWidth().intValue());
        setupImage(proportionalImageView, shot, onImageClickListener, imageUrl);
    }

    private boolean isValidImageSizes(ShotModel shot) {
        return shot.getImage().getImageHeight() != null && shot.getImage().getImageHeight() != 0
            && shot.getImage().getImageWidth() != null && shot.getImage().getImageWidth() != 0;
    }

    private void setupImage(ImageView imageView, final ShotModel shot,
        final ShotClickListener onImageClickListener, String imageUrl) {
        imageView.setVisibility(View.VISIBLE);
        imageLoader.loadTimelineImage(imageUrl, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onImageClickListener.onClick(shot);
            }
        });
    }

    private void setupVideoListener(final ShotModel reply) {
        if (reply.hasVideo()) {
            this.videoFrame.setVisibility(View.VISIBLE);
            this.videoTitle.setText(reply.getVideoTitle());
            this.videoDuration.setText(reply.getVideoDuration());
            this.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    videoClickListener.onVideoClick(reply.getVideoUrl());
                }
            });
        } else {
            this.videoFrame.setVisibility(View.GONE);
            this.videoFrame.setOnClickListener(null);
        }
    }

    private void setupNiceListener(final ShotModel reply) {
        niceButton.setChecked(reply.isMarkedAsNice());
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (reply.isMarkedAsNice()) {
                    onNiceShotListener.unmarkNice(reply.getIdShot());
                } else {
                    onNiceShotListener.markNice(reply);
                }
            }
        });
    }

    private void setupReplyClickListener(final ShotModel reply) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                replyShotClickListener.onClick(reply);
            }
        });
    }

    private void setupShotMediaContentVisibility(ShotModel shotModel) {
        if (shotModel.hasVideo() || shotModel.getImage().getImageUrl() != null) {
            shotMediaContent.setVisibility(View.VISIBLE);
        } else {
            shotMediaContent.setVisibility(View.GONE);
        }
    }
}
