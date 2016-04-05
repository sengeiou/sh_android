package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class StartedShootingViewHolder extends StreamActivityViewHolder {

    public StartedShootingViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnStreamTitleClickListener onStreamTitleClickListener, OnShotClick onShotClick) {
        super(view,
          imageLoader,
          androidTimeUtils, onAvatarClickListener, onStreamTitleClickListener, onShotClick);
    }

    @NonNull protected String getCommentPattern() {
        return getContext().getString(R.string.started_shooting_activity_text_pattern);
    }
}
