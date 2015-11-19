package com.shootr.mobile.ui.adapters;

import android.support.annotation.Nullable;
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
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotViewHolder {

    private static final int LONG_COMMENT_THRESHOLD = 20;

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
    @Bind(R.id.shot_video_title) TextView videoTitle;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;

    @BindDimen(R.dimen.nice_button_margin_top_normal) int niceMarginNormal;
    @BindDimen(R.dimen.nice_button_margin_top_short) int niceMarginShort;

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
        long shotTimestamp = shot.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), shotTimestamp));
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
            videoTitle.setText(shot.getVideoTitle());
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
        boolean moveNiceButtonUp = !hasLongComment(shot) && !hasImage(shot);
        int marginTop = moveNiceButtonUp ? niceMarginShort : niceMarginNormal;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) niceButton.getLayoutParams();
        lp.setMargins(0, marginTop, 0, 0);

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

    private boolean hasLongComment(ShotModel shot) {
        return shot.getComment() != null && shot.getComment().length() > LONG_COMMENT_THRESHOLD;
    }

    private boolean hasImage(ShotModel shot) {
        return shot.getImage() != null;
    }
}
