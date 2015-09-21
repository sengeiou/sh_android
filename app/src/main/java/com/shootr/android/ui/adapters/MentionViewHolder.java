package com.shootr.android.ui.adapters;

import android.view.View;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class MentionViewHolder extends ActivityViewHolder {

    private final OnShotClick onShotClick;

    private ActivityMentionViewHolder shotViewHolder;

    public MentionViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, onUsernameClickListener);
        this.onShotClick = onShotClick;
        shotViewHolder = new ActivityMentionViewHolder(view,
          null, onUsernameClickListener,
          androidTimeUtils,
          imageLoader,
          shotTextSpannableBuilder);
    }

    @Override
    public void render(ActivityModel activityModel, String currentUserId) {
        ShotModel shotModel = checkNotNull(activityModel.getShot());
        shotViewHolder.render(shotModel, activityModel.getUsername(), activityModel.getUserPhoto(), activityModel.getIdUser(), false);
        setShotClickListener(shotModel);
    }

    private void setShotClickListener(final ShotModel shotModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShotClick.onShotClick(shotModel);
            }
        });
    }
}
