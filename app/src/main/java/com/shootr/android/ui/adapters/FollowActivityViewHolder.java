package com.shootr.android.ui.adapters;

import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

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
