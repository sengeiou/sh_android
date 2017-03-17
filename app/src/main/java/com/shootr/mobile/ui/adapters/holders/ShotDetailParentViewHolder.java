package com.shootr.mobile.ui.adapters.holders;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.shootr.mobile.ui.widgets.BaseMessageTextView;
import com.shootr.mobile.ui.widgets.CheckableImageView;
import com.shootr.mobile.ui.widgets.ProportionalImageView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotDetailParentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.shot_detail_parent_progress) View progress;
    @BindView(R.id.shot_detail_parent_shot) View shot;
    @BindView(R.id.shot_detail_parent) FrameLayout container;
    @BindView(R.id.shot_avatar) public ImageView avatar;
    @BindView(R.id.shot_user_name) public TextView name;
    @BindView(R.id.verified_user) ImageView verifiedUser;
    @BindView(R.id.shot_timestamp) public TextView timestamp;
    @BindView(R.id.shot_text) public BaseMessageTextView text;
    @BindView(R.id.shot_image_landscape) public ProportionalImageView proportionalImageView;
    @BindView(R.id.default_image) ImageView defaultImage;
    @BindView(R.id.shot_video_frame) View videoFrame;
    @BindView(R.id.shot_video_title) TextView videoTitle;
    @BindView(R.id.shot_video_duration) TextView videoDuration;
    @BindView(R.id.shot_nice_button) CheckableImageView niceButton;
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
    private final ShotClickListener parentShotClickListener;
    private final NumberFormatUtil numberFormatUtil;
    private final Resources resources;

    public ShotDetailParentViewHolder(View itemView, ShotTextSpannableBuilder shotTextSpannableBuilder,
        OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
        AvatarClickListener avatarClickListener, ShotClickListener imageClickListener,
        OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
        ShotClickListener parentShotClickListener, NumberFormatUtil numberFormatUtil, Resources resources) {
        super(itemView);
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
        this.parentShotClickListener = parentShotClickListener;
        this.numberFormatUtil = numberFormatUtil;
        this.resources = resources;
        ButterKnife.bind(this, itemView);
    }

    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
    }

    public void bindView(final ShotModel shotModel) {
        shot.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        this.name.setText(getUsernameTitle(shotModel));
        setupVerified(shotModel);
        setupComment(shotModel);
        setupBirthData(shotModel);
        setupUserAvatar(shotModel);
        setupShotMediaContentVisibility(shotModel);
        bindImageInfo(shotModel, imageClickListener);
        setupVideoListener(shotModel);
        setupNiceListener(shotModel);
        setupParentListener(shotModel);
        bindReplyCount(shotModel);
    }

    private void setupVerified(ShotModel shotModel) {
        if (shotModel.getParentShotId() == null && shotModel.isVerifiedUser()) {
            verifiedUser.setVisibility(View.VISIBLE);
        } else {
            verifiedUser.setVisibility(View.GONE);
        }
    }

    private void setupComment(ShotModel shotModel) {
        String comment = shotModel.getComment();
        if (comment != null) {
            setComment(shotModel, comment);
        } else if (shotModel.getCtaCaption() != null) {
            setComment(shotModel, shotModel.getCtaCaption());
        } else {
            this.text.setVisibility(View.GONE);
        }
    }

    private void setComment(ShotModel shotModel, String comment) {
        CharSequence spannedComment =
          shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
        this.text.setBaseMessageModel(shotModel);
        this.text.setText(spannedComment);
        this.text.addLinks();
    }

    private void setupShotMediaContentVisibility(ShotModel shotModel) {
        if (shotModel.hasVideo() || shotModel.getImage().getImageUrl() != null) {
            shotMediaContent.setVisibility(View.VISIBLE);
        } else {
            shotMediaContent.setVisibility(View.GONE);
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

    private void setupBirthData(ShotModel shotModel) {
        long creationDate = shotModel.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));
    }

    private void setupUserAvatar(final ShotModel shotModel) {
        String photo = shotModel.getAvatar();
        imageLoader.loadProfilePhoto(photo, this.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(shotModel.getIdUser());
            }
        });
    }

    private void bindImageInfo(final ShotModel shot,
        ShotClickListener onImageClickListener) {
        String imageUrl = shot.getImage().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (isValidImageSizes(shot)) {
                setupProportionalImageView(shot, onImageClickListener, imageUrl);
            } else {
                proportionalImageView.setVisibility(View.GONE);
                setupImage(defaultImage, shot, onImageClickListener,
                    imageUrl);
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

    private void setupVideoListener(final ShotModel shotModel) {
        if (shotModel.hasVideo()) {
            this.videoFrame.setVisibility(View.VISIBLE);
            this.videoTitle.setText(shotModel.getVideoTitle());
            this.videoDuration.setText(shotModel.getVideoDuration());
            this.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    videoClickListener.onVideoClick(shotModel.getVideoUrl());
                }
            });
        } else {
            this.videoFrame.setVisibility(View.GONE);
            this.videoFrame.setOnClickListener(null);
        }
    }

    private void setupNiceListener(final ShotModel shotModel) {
        niceButton.setChecked(shotModel.isMarkedAsNice());
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (shotModel.isMarkedAsNice()) {
                    onNiceShotListener.unmarkNice(shotModel.getIdShot());
                } else {
                    onNiceShotListener.markNice(shotModel);
                }
            }
        });
    }

    private void setupParentListener(final ShotModel shotModel) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                parentShotClickListener.onClick(shotModel);
            }
        });
    }

    private String getUsernameTitle(ShotModel shotModel) {
        if (shotModel.isReply()) {
            return resources.getString(R.string.reply_name_pattern,
              shotModel.getUsername(),
              shotModel.getReplyUsername());
        } else {
            return shotModel.getUsername();
        }
    }
}
