package com.shootr.android.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnEventTitleClickListener;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;

public class StartedShootingViewHolder extends ClickableEventActivityViewHolder {

    public StartedShootingViewHolder(View view, PicassoWrapper picasso, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener, OnEventTitleClickListener onEventTitleClickListener) {
        super(view,
          picasso,
          androidTimeUtils,
          shotTextSpannableBuilder,
          onAvatarClickListener,
          usernameClickListener,
          onEventTitleClickListener);
    }

    @NonNull protected String getPatternText() {
        return getContext().getString(R.string.started_shooting_activity_text_pattern);
    }
}