package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class GenericActivityViewHolder extends RecyclerView.ViewHolder {

    protected final ImageLoader imageLoader;
    private final AndroidTimeUtils androidTimeUtils;
    private final OnAvatarClickListener onAvatarClickListener;

    @BindView(R.id.activity_avatar) AvatarView avatar;
    @BindView(R.id.ativity_user_name) TextView name;
    @BindView(R.id.activity_timestamp) TextView elapsedTime;
    @BindView(R.id.activity_text) ClickableTextView text;
    @BindView(R.id.shot_image) ImageView image;
    @BindView(R.id.activity_follow_button) FollowButton followButton;
    @BindView(R.id.favorite_stream_indicator) ShineButton favoriteButton;
    @BindView(R.id.stream_name) TextView streamName;

    public GenericActivityViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener) {
        super(view);
        this.imageLoader = imageLoader;
        this.androidTimeUtils = androidTimeUtils;
        this.onAvatarClickListener = onAvatarClickListener;
        ButterKnife.bind(this, view);
    }

    public void render(final ActivityModel activity) {
        renderText(activity);
        renderName(activity);
        renderElapsedTime(activity);
        renderAvatar(activity);
        renderImage(activity);
        renderFavorite(activity);
        renderStreamName(activity);
    }

    protected void renderText(ActivityModel activity) {
        if (activity.getComment() != null) {
            text.setText(activity.getComment());
        }
    }

    protected void renderName(ActivityModel activity) {
        name.setText(activity.getUsername());
    }

    protected void renderElapsedTime(ActivityModel activity) {
        elapsedTime.setText(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()));
    }

    protected void renderAvatar(final ActivityModel activity) {
        imageLoader.loadProfilePhoto(activity.getUserPhoto(), avatar, activity.getUsername());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onAvatarClickListener.onAvatarClick(activity.getIdUser(), avatar);
            }
        });
    }

    protected void renderImage(ActivityModel activity) {
        image.setVisibility(View.GONE);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    protected void renderFavorite(ActivityModel activityModel) {
        favoriteButton.setVisibility(View.GONE);
    }

    protected void renderStreamName(final ActivityModel activityModel) {
        if (activityModel.getStreamTitle() != null && !activityModel.getStreamTitle().isEmpty()) {
            streamName.setVisibility(View.VISIBLE);
            streamName.setText(activityModel.getStreamTitle());

            streamName.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (activityModel.getIdStream() != null) {
                        view.getContext()
                            .startActivity(StreamTimelineActivity.newIntent(view.getContext(),
                                activityModel.getIdStream()));
                    }
                }
            });
        }
    }
}
