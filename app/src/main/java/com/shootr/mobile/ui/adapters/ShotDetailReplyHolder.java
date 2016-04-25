package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NicerTextSpannableBuilder;

public class ShotDetailReplyHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.shot_reply_container) RelativeLayout container;
    @Bind(R.id.shot_avatar) public ImageView avatar;
    @Bind(R.id.shot_user_name) public TextView name;
    @Bind(R.id.shot_timestamp) public TextView timestamp;
    @Bind(R.id.shot_text) public ClickableTextView text;
    @Bind(R.id.shot_image) public ImageView image;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
    private final NicerTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener replyShotClickListener;

    public ShotDetailReplyHolder(View itemView, NicerTextSpannableBuilder shotTextSpannableBuilder,
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

        long creationDate = reply.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));

        String photo = reply.getPhoto();
        imageLoader.loadProfilePhoto(photo, this.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(reply.getIdUser());
            }
        });

        String imageUrl = reply.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.image.setVisibility(View.VISIBLE);
            imageLoader.loadTimelineImage(imageUrl, this.image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    imageClickListener.onClick(reply);
                }
            });
        } else {
            this.image.setVisibility(View.GONE);
        }

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

        container.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                replyShotClickListener.onClick(reply);
            }
        });
    }
}
