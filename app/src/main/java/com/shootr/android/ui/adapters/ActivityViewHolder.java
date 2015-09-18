package com.shootr.android.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private final ImageLoader imageLoader;
    private final AndroidTimeUtils androidTimeUtils;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnAvatarClickListener onAvatarClickListener;
    private final OnUsernameClickListener onUsernameClickListener;

    @Nullable @Bind(R.id.activity_avatar) ImageView avatar;
    @Nullable @Bind(R.id.ativity_user_name) TextView name;
    @Nullable @Bind(R.id.activity_timestamp) TextView elapsedTime;
    @Nullable @Bind(R.id.activity_text) ClickableTextView text;

    public ActivityViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener) {
        super(view);
        this.imageLoader = imageLoader;
        this.androidTimeUtils = androidTimeUtils;
        this.onAvatarClickListener = onAvatarClickListener;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
        ButterKnife.bind(this, view);
    }

    public void render(final ActivityModel activity, String currentUserId) {
        checkNotNull(name);
        checkNotNull(avatar);
        checkNotNull(elapsedTime);

        name.setText(activity.getUsername());
        text.setText(formatActivityComment(activity, currentUserId));
        elapsedTime.setText(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()));
        imageLoader.loadProfilePhoto(activity.getUserPhoto(), avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClickListener.onAvatarClick(activity.getIdUser(), avatar);
            }
        });
    }

    protected CharSequence formatActivityComment(final ActivityModel activity, String currentUserId) {
        if (activity.getIdTargetUser() != null && activity.getIdTargetUser().equals(currentUserId)) {
            activity.setComment(itemView.getContext().getString(R.string.activity_started_following_you));
        }
        return shotTextSpannableBuilder.formatWithUsernameSpans(activity.getComment(), onUsernameClickListener);
    }


    private Context getContext() {
        return itemView.getContext();
    }
}
