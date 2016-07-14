package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NicerTextSpannableBuilder;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;
import java.util.Date;

public class ShotDetailMainViewHolder extends RecyclerView.ViewHolder {

    public static final int NICES_TRESHOLD = 2;
    private Context context;

    @Bind(R.id.shot_detail_avatar) ImageView avatar;
    @Bind(R.id.shot_detail_user_name) TextView username;
    @Bind(R.id.shot_detail_timestamp) TextView timestamp;
    @Bind(R.id.shot_detail_text) ClickableEmojiconTextView shotText;
    @Bind(R.id.shot_detail_image) ImageView shotImage;
    @Bind(R.id.shot_detail_stream_title) TextView streamTitle;
    @Bind(R.id.shot_detail_parent_toggle) ImageView parentToggleButton;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
    @Bind(R.id.shot_nice_count) TextView niceCount;
    @Bind(R.id.shot_nicers) ClickableTextView nicers;
    @Bind(R.id.shot_detail_pin_to_profile_container) LinearLayout pinToProfileContainer;

    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener streamClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final TimeFormatter timeFormatter;
    private final Resources resources;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener onClickListenerPinToProfile;
    private final ShotClickListener nicesClickListener;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final NicerTextSpannableBuilder nicerTextSpannableBuilder;

    public ShotDetailMainViewHolder(View itemView, ImageLoader imageLoader,
      AvatarClickListener avatarClickListener, ShotClickListener streamClickListener,
      ShotClickListener imageClickListener, OnVideoClickListener videoClickListener,
      OnUsernameClickListener onUsernameClickListener, TimeFormatter timeFormatter, Resources resources,
      OnNiceShotListener onNiceShotListener, ShotClickListener onClickListenerPinToProfile,
      ShotClickListener nicesClickListener, ShotTextSpannableBuilder shotTextSpannableBuilder,
      NicerTextSpannableBuilder nicerTextSpannableBuilder) {
        super(itemView);
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.streamClickListener = streamClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeFormatter = timeFormatter;
        this.resources = resources;
        this.onNiceShotListener = onNiceShotListener;
        this.onClickListenerPinToProfile = onClickListenerPinToProfile;
        this.nicesClickListener = nicesClickListener;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.nicerTextSpannableBuilder = nicerTextSpannableBuilder;
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void bindView(final ShotModel shotModel) {
        username.setText(getUsernameTitle(shotModel));
        timestamp.setText(getTimestampForDate(shotModel.getBirth()));
        setupComment(shotModel);
        showStreamTitle(shotModel);
        setupAvatar(shotModel);
        setupImage(shotModel);
        setupVideo(shotModel);
        setupReply(shotModel);
        setupNiceButton(shotModel);
        setupPinToProfileContainer(shotModel);
    }

    public void setupReply(ShotModel shotModel) {
        if (shotModel.isReply()) {
            setReply();
        } else {
            parentToggleButton.setVisibility(View.GONE);
        }
    }

    public void setupVideo(ShotModel shotModel) {
        if (shotModel.hasVideo()) {
            setVideo(shotModel);
        } else {
            this.videoFrame.setVisibility(View.GONE);
            this.videoFrame.setOnClickListener(null);
        }
    }

    public void setupImage(ShotModel shotModel) {
        String imageUrl = shotModel.getImage().getImageUrl();
        if (imageUrl != null) {
            setImage(shotModel, imageUrl);
        } else {
            shotImage.setVisibility(View.GONE);
        }
    }

    public void setupComment(ShotModel shotModel) {
        String comment = shotModel.getComment();
        if (comment != null) {
            setComment(comment);
        } else {
            shotText.setVisibility(View.GONE);
        }
    }

    public void setupAvatar(final ShotModel shotModel) {
        imageLoader.loadProfilePhoto(shotModel.getPhoto(), avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                avatarClickListener.onClick(shotModel.getIdUser());
            }
        });
    }

    public void setupNiceButton(final ShotModel shotModel) {
        Integer nicesCount = shotModel.getNiceCount();
        if (nicesCount > 0) {
            setNiceCount(shotModel, nicesCount);
        } else {
            this.niceCount.setVisibility(View.GONE);
            this.nicers.setVisibility(View.GONE);
        }
        setNiceButton(shotModel);
    }

    public void setNiceButton(final ShotModel shotModel) {
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

    public void setNiceCount(final ShotModel shotModel, Integer niceCount) {
        if (niceCount > NICES_TRESHOLD) {
            this.nicers.setVisibility(View.GONE);
            this.niceCount.setVisibility(View.VISIBLE);
            this.niceCount.setText(context.getResources()
              .getQuantityString(R.plurals.nice_count_pattern, niceCount, niceCount));
            this.niceCount.setTextColor(context.getResources().getColor(R.color.links));
            this.niceCount.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    nicesClickListener.onClick(shotModel);
                }
            });
        } else {
            this.nicers.setVisibility(View.VISIBLE);
            this.niceCount.setVisibility(View.GONE);
            setNicers(shotModel);
        }
    }

    private void setNicers(ShotModel shotModel) {
        if (shotModel.getNicers() != null) {
            String nicersText = context.getString(R.string.niced_by);
            for (String nicer : shotModel.getNicers()) {
                nicersText += nicer + ", ";
            }
            this.nicers.setText(nicerTextSpannableBuilder.formatWithUsernameSpans(nicersText,
              onUsernameClickListener));
        }
    }

    public void setComment(String comment) {
        CharSequence spannedComment =
          shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
        shotText.setText(spannedComment);
        shotText.addLinks();
    }

    public void setImage(final ShotModel shotModel, String imageUrl) {
        shotImage.setVisibility(View.VISIBLE);
        imageLoader.loadTimelineImage(imageUrl, shotImage);
        shotImage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                imageClickListener.onClick(shotModel);
            }
        });
    }

    public void setVideo(final ShotModel shotModel) {
        this.videoFrame.setVisibility(View.VISIBLE);
        this.shotImage.setVisibility(View.VISIBLE);
        this.videoTitle.setText(shotModel.getVideoTitle());
        this.videoDuration.setText(shotModel.getVideoDuration());
        this.videoFrame.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                videoClickListener.onVideoClick(shotModel.getVideoUrl());
            }
        });
    }

    public void setReply() {
        parentToggleButton.setVisibility(View.VISIBLE);
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

    private String getTimestampForDate(Date date) {
        return timeFormatter.getDateAndTimeDetailed(date.getTime());
    }

    private void showStreamTitle(final ShotModel shotModel) {
        String title = shotModel.getStreamTitle();
        if (title != null) {
            streamTitle.setText(shotModel.getStreamTitle());
            streamTitle.setVisibility(View.VISIBLE);
        } else {
            streamTitle.setVisibility(View.GONE);
        }
        streamTitle.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                streamClickListener.onClick(shotModel);
            }
        });
    }

    public void disableStreamTitle() {
        streamTitle.setTextColor(context.getResources().getColor(R.color.gray_material));
        streamTitle.setEnabled(false);
    }

    public void enableStreamTitle() {
        streamTitle.setTextColor(context.getResources().getColor(R.color.links));
        streamTitle.setEnabled(true);
    }

    private void setupPinToProfileContainer(final ShotModel shotModel) {
        pinToProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onClickListenerPinToProfile.onClick(shotModel);
            }
        });
    }

    public void hidePintToProfileContainer() {
        pinToProfileContainer.setVisibility(View.GONE);
    }

    public void showPintToProfileContainer() {
        pinToProfileContainer.setVisibility(View.VISIBLE);
    }
}
