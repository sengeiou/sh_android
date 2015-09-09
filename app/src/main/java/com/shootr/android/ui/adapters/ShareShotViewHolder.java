package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class ShareShotViewHolder extends ActivityViewHolder {

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final OnShotClick onShotClick;

    @BindString(R.string.activity_share_shot_text_base) String shareShotTextBase;
    private ShotViewHolder shotViewHolder;

    public ShareShotViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener,
      OnImageClickListener onImageClickListener,
      OnVideoClickListener onVideoClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, onUsernameClickListener);
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
        this.onShotClick = onShotClick;
        shotViewHolder = new ShotViewHolder(view,
          onAvatarClickListener,
          onImageClickListener,
          onVideoClickListener,
          null, onUsernameClickListener,
          androidTimeUtils,
          imageLoader,
          shotTextSpannableBuilder);
    }

    @Override
    public void render(ActivityModel activityModel, String currentUserId) {
        ShotModel shotModel = checkNotNull(activityModel.getShot());
        shotViewHolder.niceButton.setVisibility(View.GONE);
        shotViewHolder.render(shotModel, false);
        setShotClickListener(shotModel);
        renderShareShot(activityModel);
    }

    private void setShotClickListener(final ShotModel shotModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShotClick.onShotClick(shotModel);
            }
        });
    }

    private void renderShareShot(ActivityModel activity) {
        String shareShotText = String.format(shareShotTextBase, activity.getUsername());
        CharSequence shareShotTextFormatted =
          shotTextSpannableBuilder.formatWithUsernameSpans(shareShotText, onUsernameClickListener);
        text.setText(shareShotTextFormatted);
    }

}