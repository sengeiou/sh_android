package com.shootr.android.ui.adapters;

import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.ui.widgets.NiceButtonView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

public class ShotViewHolder {

    private final OnAvatarClickListener avatarClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ImageLoader imageLoader;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;

    @Bind(R.id.shot_avatar) ImageView avatar;
    @Bind(R.id.shot_user_name) TextView name;
    @Bind(R.id.shot_timestamp) TextView timestamp;
    @Bind(R.id.shot_text) ClickableTextView text;
    @Bind(R.id.shot_image)  ImageView image;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;

    @BindColor(R.color.short_title_color) int shortTitleColor;
    public int position;
    private View view;

    public ShotViewHolder(View view,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener,
      AndroidTimeUtils timeUtils,
      ImageLoader imageLoader,
      ShotTextSpannableBuilder shotTextSpannableBuilder) {
        this.avatarClickListener = avatarClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.imageLoader = imageLoader;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        ButterKnife.bind(this, view);
        this.view = view;
    }

    protected void render(ShotModel shot, boolean shouldShowShortTitle) {
        bindUsername(shot);
        bindComment(shot, shouldShowShortTitle);
        bindElapsedTime(shot);
        bindUserPhoto(shot);
        bindImageInfo(shot);
        bindVideoInfo(shot);
        bindNiceInfo(shot);
    }

    protected void bindComment(ShotModel item, boolean shouldShowShortTitle) {
        String comment = item.getComment();
        String shortTitle = null;
        if (shouldShowShortTitle && item.getStreamShortTitle() != null) {
            shortTitle = item.getStreamShortTitle();
        }

        SpannableStringBuilder commentWithShortTitle = buildCommentTextWithShortTitle(comment, shortTitle);
        if (commentWithShortTitle != null) {
            addShotComment(this, commentWithShortTitle);
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    private @Nullable
    SpannableStringBuilder buildCommentTextWithShortTitle(@Nullable String comment, @Nullable String shortTitle) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (comment == null && shortTitle == null) {
            return null;
        }
        if (comment != null) {
            builder.append(comment);
        }
        if (comment != null && shortTitle != null) {
            builder.append(" ");
        }
        if (shortTitle != null) {
            builder.append(formatShortTitle(shortTitle));
        }
        return builder;
    }

    private SpannableString formatShortTitle(String shortTitle) {
        ForegroundColorSpan span = new ForegroundColorSpan(shortTitleColor);

        SpannableString shortTitleSpan = new SpannableString(shortTitle);
        shortTitleSpan.setSpan(span, 0, shortTitleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return shortTitleSpan;
    }

    private void addShotComment(ShotViewHolder vh, CharSequence comment) {
        CharSequence spannedComment = shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
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
        return view.getContext().getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
    }

    private void bindElapsedTime(ShotModel shot) {
        long timestamp = shot.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), timestamp));
    }

    private void bindUserPhoto(final ShotModel shot) {
        imageLoader.loadProfilePhoto(shot.getPhoto(), avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarClickListener.onAvatarClick(shot.getIdUser(), v);
            }
        });
    }

    private void bindImageInfo(final ShotModel shot) {
        String imageUrl = shot.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            image.setVisibility(View.VISIBLE);
            imageLoader.loadTimelineImage(imageUrl, image);
        } else {
            image.setVisibility(View.GONE);
        }
    }

    private void bindVideoInfo(final ShotModel shot) {
        if (shot.hasVideo()) {
            videoFrame.setVisibility(View.VISIBLE);
            videoDuration.setText(shot.getVideoDuration());
            videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoClickListener.onVideoClick(shot.getVideoUrl());
                }
            });
        } else {
            videoFrame.setVisibility(View.GONE);
            videoFrame.setOnClickListener(null);
        }
    }

    private void bindNiceInfo(final ShotModel shot) {
        niceButton.setChecked(shot.isMarkedAsNice());
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shot.isMarkedAsNice()) {
                    onNiceShotListener.unmarkNice(shot.getIdShot());
                } else {
                    onNiceShotListener.markNice(shot.getIdShot());
                }
            }
        });
    }
}
