package com.shootr.mobile.ui.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.CheckableImageView;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotDetailParentViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.shot_detail_parent_progress) View progress;
    @Bind(R.id.shot_detail_parent_shot) View shot;
    @Bind(R.id.shot_detail_parent) FrameLayout container;
    @Bind(R.id.shot_avatar) public ImageView avatar;
    @Bind(R.id.shot_user_name) public TextView name;
    @Bind(R.id.shot_timestamp) public TextView timestamp;
    @Bind(R.id.shot_text) public ClickableTextView text;
    @Bind(R.id.shot_image) public ImageView image;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) CheckableImageView niceButton;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener parentShotClickListener;
    private final Resources resources;

    public ShotDetailParentViewHolder(View itemView, ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      AvatarClickListener avatarClickListener, ShotClickListener imageClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      ShotClickListener parentShotClickListener, Resources resources) {
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
        setupComment(shotModel);
        setupBirthData(shotModel);
        setupUserAvatar(shotModel);
        setupImage(shotModel);
        setupVideoListener(shotModel);
        setupNiceListener(shotModel);
        setupParentListener(shotModel);
    }

    private void setupComment(ShotModel shotModel) {
        String comment = shotModel.getComment();
        if (comment != null) {
            CharSequence spannedComment =
              shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
            this.text.setText(spannedComment);
            this.text.addLinks();
        } else {
            this.text.setVisibility(View.GONE);
        }
    }

    private void setupBirthData(ShotModel shotModel) {
        long creationDate = shotModel.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));
    }

    private void setupUserAvatar(final ShotModel shotModel) {
        String photo = shotModel.getPhoto();
        imageLoader.loadProfilePhoto(photo, this.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(shotModel.getIdUser());
            }
        });
    }

    private void setupImage(final ShotModel shotModel) {
        String imageUrl = shotModel.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.image.setVisibility(View.VISIBLE);
            imageLoader.loadTimelineImage(imageUrl, this.image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    imageClickListener.onClick(shotModel);
                }
            });
        } else {
            this.image.setVisibility(View.GONE);
        }
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
                    onNiceShotListener.markNice(shotModel.getIdShot());
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
