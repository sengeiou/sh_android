package com.shootr.android.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;

public class StartedShootingViewHolder extends ClickableStreamActivityViewHolder {

    public StartedShootingViewHolder(View view, PicassoWrapper picasso, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view,
          picasso,
          androidTimeUtils,
          shotTextSpannableBuilder,
          onAvatarClickListener, onUsernameClickListener, onStreamTitleClickListener);
    }

    @NonNull protected String getPatternText() {
        return getContext().getString(R.string.started_shooting_activity_text_pattern);
    }
}
