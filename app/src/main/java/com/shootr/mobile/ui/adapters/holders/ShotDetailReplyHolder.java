package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.AvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableEmojiconTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotDetailReplyHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.shot_reply_container) RelativeLayout container;
    @Bind(R.id.shot_avatar) public ImageView avatar;
    @Bind(R.id.shot_user_name) public TextView name;
    @Bind(R.id.shot_timestamp) public TextView timestamp;
    @Bind(R.id.shot_text) public ClickableEmojiconTextView text;
    @Bind(R.id.shot_image_landscape) ImageView imageLandscape;
    @Bind(R.id.shot_image_portrait) ImageView imagePortrait;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener replyShotClickListener;

    public ShotDetailReplyHolder(View itemView, ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      AvatarClickListener avatarClickListener, ShotClickListener imageClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      ShotClickListener replyShotClickListener) {
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
        ButterKnife.bind(this, itemView);
    }

    public void bindView(final ShotModel reply) {
        this.name.setText(reply.getUsername());
        setupComment(reply);
        setupBirthData(reply);
        loadImage(reply);
        bindImageInfo(reply, imageClickListener);
        setupVideoListener(reply);
        setupNiceListener(reply);
        setupReplyClickListener(reply);
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

    private void loadImage(final ShotModel reply) {
        String photo = reply.getPhoto();
        imageLoader.loadProfilePhoto(photo, this.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(reply.getIdUser());
            }
        });
    }

    private void bindImageInfo(final ShotModel shot,
        ShotClickListener onImageClickListener) {
        String imageUrl = shot.getImage().getImageUrl();
        Long imageWidth = shot.getImage().getImageWidth();
        Long imageHeight = shot.getImage().getImageHeight();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            handleImage(shot, onImageClickListener, imageUrl,
                imageWidth, imageHeight);
        } else {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.GONE);
        }
    }

    private void handleImage(final ShotModel shot, ShotClickListener onImageClickListener,
        String imageUrl, Long imageWidth, Long imageHeight) {
        if (isImageValid(imageWidth, imageHeight)) {
            setImageLayout(shot, onImageClickListener, imageUrl,
                imageWidth, imageHeight);
        } else {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.VISIBLE);
            setupImage(imageLandscape, shot, onImageClickListener,
                imageUrl);
        }
    }

    private void setImageLayout(final ShotModel shot,
        ShotClickListener onImageClickListener, String imageUrl, Long imageWidth,
        Long imageHeight) {
        if (imageWidth > imageHeight) {
            imagePortrait.setVisibility(View.GONE);
            imageLandscape.setVisibility(View.VISIBLE);
            setupImage(imageLandscape, shot, onImageClickListener,
                imageUrl);
        } else {
            imageLandscape.setVisibility(View.GONE);
            imagePortrait.setVisibility(View.VISIBLE);
            setupImage(imagePortrait, shot, onImageClickListener,
                imageUrl);
        }
    }

    private void setupImage(ImageView imageView, final ShotModel shot,
        final ShotClickListener onImageClickListener, String imageUrl) {
        imageLoader.loadTimelineImage(imageUrl, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onImageClickListener.onClick(shot);
            }
        });
    }

    private boolean isImageValid(Long imageWidth, Long imageHeight) {
        return imageWidth != null && imageWidth != 0 && imageHeight != null && imageHeight != 0;
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
                    onNiceShotListener.markNice(reply.getIdShot());
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
}
