package com.shootr.android.ui.adapters;

import android.view.View;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.adapters.listeners.UsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class NiceShotViewHolder extends ActivityViewHolder {

    private ShotViewHolder shotViewHolder;

    public NiceShotViewHolder(View view,
      PicassoWrapper picasso,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener,
      OnImageClickListener onImageClickListener,
      OnVideoClickListener onVideoClickListener) {
        super(view, picasso, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, usernameClickListener);
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
        shotViewHolder.niceButton.setVisibility(View.GONE);
        text.setText(activityModel.getComment());
        ShotModel shotModel = checkNotNull(activityModel.getShot());
        shotViewHolder.render(shotModel, false);
    }
}
