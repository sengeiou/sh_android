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
import com.shootr.android.ui.adapters.listeners.NiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.ui.widgets.NiceButtonView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;

public class ShotViewHolder {

    private final OnVideoClickListener videoClickListener;
    private final NiceShotListener niceShotListener;
    private final UsernameClickListener usernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final PicassoWrapper picasso;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;

    @Bind(R.id.shot_avatar) ImageView avatar;
    @Bind(R.id.shot_user_name) TextView name;
    @Bind(R.id.shot_timestamp) TextView timestamp;
    @Bind(R.id.shot_text) ClickableTextView text;
    @Bind(R.id.shot_image)  ImageView image;
    @Bind(R.id.shot_video_frame) View videoFrame;
    @Bind(R.id.shot_video_duration) TextView videoDuration;
    @Bind(R.id.shot_nice_button) NiceButtonView niceButton;

    @BindColor(R.color.tag_color) int tagColor;
    public int position;
    private View view;

    public ShotViewHolder(View view,
      View.OnClickListener avatarClickListener,
      View.OnClickListener imageClickListener,
      OnVideoClickListener videoClickListener,
      NiceShotListener niceShotListener,
      UsernameClickListener usernameClickListener,
      AndroidTimeUtils timeUtils,
      PicassoWrapper picasso,
      ShotTextSpannableBuilder shotTextSpannableBuilder) {
        this.videoClickListener = videoClickListener;
        this.niceShotListener = niceShotListener;
        this.usernameClickListener = usernameClickListener;
        this.timeUtils = timeUtils;
        this.picasso = picasso;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        ButterKnife.bind(this, view);
        this.view = view;
        avatar.setOnClickListener(avatarClickListener);
        image.setOnClickListener(imageClickListener);
    }

    protected void render(ShotModel item, boolean shouldShowTag) {
        bindUsername(item);
        bindComment(item, shouldShowTag);
        bindElapsedTime(item);
        bindPhoto(item);
        bindImageInfo(item);
        bindVideoInfo(item);
        bindNiceInfo(item);
    }

    protected void bindComment(ShotModel item, boolean shouldShowTag) {
        String comment = item.getComment();
        String tag = null;
        if (shouldShowTag && item.getStreamTag() != null) {
            tag = item.getStreamTag();
        }

        SpannableStringBuilder commentWithTag = buildCommentTextWithTag(comment, tag);
        if (commentWithTag != null) {
            addShotComment(this, commentWithTag);
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    private @Nullable
    SpannableStringBuilder buildCommentTextWithTag(@Nullable String comment, @Nullable String tag) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (comment == null && tag == null) {
            return null;
        }
        if (comment != null) {
            builder.append(comment);
        }
        if (comment != null && tag != null) {
            builder.append(" ");
        }
        if (tag != null) {
            builder.append(formatTag(tag));
        }
        return builder;
    }

    private SpannableString formatTag(String tag) {
        ForegroundColorSpan span = new ForegroundColorSpan(tagColor);

        SpannableString tagSpan = new SpannableString(tag);
        tagSpan.setSpan(span, 0, tagSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tagSpan;
    }

    private void addShotComment(ShotViewHolder vh, CharSequence comment) {
        CharSequence spannedComment = shotTextSpannableBuilder.formatWithUsernameSpans(comment,
          usernameClickListener);
        vh.text.setText(spannedComment);
        vh.text.addLinks();
    }

    protected void bindUsername(ShotModel item) {
        String usernameTitle = item.getUsername();
        if (item.isReply()) {
            name.setText(getReplyName(item));
        } else {
            name.setText(usernameTitle);
        }
    }

    private String getReplyName(ShotModel item) {
        return view.getContext().getString(R.string.reply_name_pattern, item.getUsername(), item.getReplyUsername());
    }

    protected void bindElapsedTime(ShotModel item) {
        long timestamp = item.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), timestamp));
    }

    protected void bindPhoto(ShotModel item) {
        String photo = item.getPhoto();
        picasso.loadProfilePhoto(photo).into(avatar);
        avatar.setTag(this);
        image.setTag(this);
    }

    protected void bindImageInfo(ShotModel item) {
        String imageUrl = item.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            image.setVisibility(View.VISIBLE);
            picasso.loadTimelineImage(imageUrl).into(image);
        } else {
            image.setVisibility(View.GONE);
        }
    }

    protected void bindVideoInfo(final ShotModel item) {
        if (item.hasVideo()) {
            videoFrame.setVisibility(View.VISIBLE);
            videoDuration.setText(item.getVideoDuration());
            videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoClickListener.onClick(item.getVideoUrl());
                }
            });
        } else {
            videoFrame.setVisibility(View.GONE);
            videoFrame.setOnClickListener(null);
        }
    }

    private void bindNiceInfo(final ShotModel item) {
        niceButton.setChecked(item.isMarkedAsNice());
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isMarkedAsNice()) {
                    niceShotListener.unmarkNice(item.getIdShot());
                } else {
                    niceShotListener.markNice(item.getIdShot());
                }
            }
        });
    }
}
