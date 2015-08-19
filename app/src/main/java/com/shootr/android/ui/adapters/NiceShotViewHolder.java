package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.adapters.listeners.UsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class NiceShotViewHolder extends ActivityViewHolder {

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final UsernameClickListener usernameClickListener;
    private final OnShotClick onShotClick;

    @BindString(R.string.activity_nice_shot_text_base) String niceShotTextBase;
    private ShotViewHolder shotViewHolder;

    public NiceShotViewHolder(View view,
      PicassoWrapper picasso,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener,
      OnImageClickListener onImageClickListener,
      OnVideoClickListener onVideoClickListener, OnShotClick onShotClick) {
        super(view, picasso, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, usernameClickListener);
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.usernameClickListener = usernameClickListener;
        this.onShotClick = onShotClick;
        shotViewHolder = new ShotViewHolder(view,
          onAvatarClickListener,
          onImageClickListener,
          onVideoClickListener,
          null,
          usernameClickListener,
          androidTimeUtils,
          picasso,
          shotTextSpannableBuilder);
    }

    @Override
    public void render(ActivityModel activityModel) {
        ShotModel shotModel = checkNotNull(activityModel.getShot());
        shotViewHolder.niceButton.setVisibility(View.GONE);
        shotViewHolder.render(shotModel, false);
        setShotClickListener(shotModel);
        renderNiceShot(activityModel);
    }

    private void setShotClickListener(final ShotModel shotModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShotClick.onClick(shotModel);
            }
        });
    }

    private void renderNiceShot(ActivityModel activity) {
        String niceShotText = String.format(niceShotTextBase, activity.getUsername());
        CharSequence niceShotTextFormatted =
          shotTextSpannableBuilder.formatWithUsernameSpans(niceShotText, usernameClickListener);
        text.setText(niceShotTextFormatted);
    }
}
