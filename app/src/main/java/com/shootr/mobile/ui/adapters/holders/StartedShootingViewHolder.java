package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class StartedShootingViewHolder extends StreamActivityViewHolder {

    public StartedShootingViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClick);
    }

    @NonNull protected String getCommentPattern() {
        return getContext().getString(R.string.started_shooting_activity_text_pattern);
    }
}
