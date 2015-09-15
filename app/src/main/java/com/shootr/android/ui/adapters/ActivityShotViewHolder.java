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
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

public class ActivityShotViewHolder {

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
    @Bind(R.id.shot_text) TextView text;
    @Bind(R.id.shot_image)  ImageView image;

    @BindColor(R.color.tag_color) int tagColor;
    @BindString(R.string.niced_shot_activity) String nicedShot;
    @BindString(R.string.shared_shot_activity) String sharedShot;
    @BindString(R.string.niced_shot_activity_with_comment) String nicedShotWithComment;
    @BindString(R.string.shared_shot_activity_with_comment) String sharedShotWithComment;

    public int position;
    private View view;

    public ActivityShotViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
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

    protected void render(ShotModel shot, String username, String photo, String idUser, boolean shouldShowTag, boolean isNice) {
        bindUsername(username);
        bindElapsedTime(shot);
        bindComment(shot,shouldShowTag,isNice);
        bindUserPhoto(photo, idUser);
        bindImageInfo(shot);
    }

    protected void bindComment(ShotModel item, boolean shouldShowTag, boolean isNice) {
        String comment = item.getComment();
        String tag = null;
        if (shouldShowTag && item.getStreamTag() != null) {
            tag = item.getStreamTag();
        }
        SpannableStringBuilder commentWithTag = createComment(item, isNice, comment, tag);
        if (commentWithTag != null) {
            addShotComment(this, commentWithTag);
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    private SpannableStringBuilder createComment(ShotModel item, boolean isNice, String comment, String tag) {
        SpannableStringBuilder commentWithTag;
        if (comment == null) {
            String resultComment;
            if (isNice) {
                resultComment = nicedShot;
            } else {
                resultComment = String.format(sharedShot, item.getUsername());
            }
            commentWithTag = buildCommentTextWithTag(resultComment, tag);
        } else {
            String resultComment;
            if (isNice) {
                resultComment = String.format(nicedShotWithComment, comment);
            } else {
                resultComment = String.format(sharedShotWithComment, item.getUsername(), comment);
            }
            commentWithTag = buildCommentTextWithTag(resultComment, tag);
        }
        return commentWithTag;
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

    private void addShotComment(ActivityShotViewHolder vh, CharSequence comment) {
        CharSequence spannedComment = shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
        vh.text.setText(spannedComment);
    }

    private void bindUsername(String username) {
        name.setText(username);
    }

    private void bindElapsedTime(ShotModel shot) {
        long timestamp = shot.getBirth().getTime();
        this.timestamp.setText(timeUtils.getElapsedTime(view.getContext(), timestamp));
    }

    private void bindUserPhoto(String url, final String idUser) {
        imageLoader.loadProfilePhoto(url, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarClickListener.onAvatarClick(idUser, v);
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

}
