package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class OpenedViewHolder extends ClickableStreamActivityViewHolder {

    public OpenedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
        OnAvatarClickListener onAvatarClickListener,
        OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onStreamTitleClickListener,
            onFollowUnfollowListener);
    }

    @NonNull protected String getCommentPattern() {
        return getContext().getString(R.string.opened_activity_text_pattern);
    }
}
