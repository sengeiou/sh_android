package com.shootr.mobile.ui.adapters;

import android.view.View;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class FollowActivityViewHolder extends GenericActivityViewHolder {

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;

    private String currentUserId;

    public FollowActivityViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    protected void renderText(ActivityModel activity) {
        checkNotNull(currentUserId,
          "Follow ViewHolder must know the current user's id. Use setCurrentUser(String) before rendering");
        text.setText(formatActivityComment(activity, currentUserId));
    }

    protected CharSequence formatActivityComment(final ActivityModel activity, String currentUserId) {
        if (activity.getIdTargetUser() != null && activity.getIdTargetUser().equals(currentUserId)) {
            activity.setComment(itemView.getContext().getString(R.string.activity_started_following_you));
        }
        return shotTextSpannableBuilder.formatWithUsernameSpans(activity.getComment(), onUsernameClickListener);
    }
}
