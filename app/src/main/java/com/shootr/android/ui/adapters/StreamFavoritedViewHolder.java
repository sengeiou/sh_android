package com.shootr.android.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

public class StreamFavoritedViewHolder extends ClickableStreamActivityViewHolder {

    public StreamFavoritedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view,
          imageLoader,
          androidTimeUtils,
          shotTextSpannableBuilder,
          onAvatarClickListener, onUsernameClickListener, onStreamTitleClickListener);
    }

    @NonNull protected String getPatternText() {
        return getContext().getString(R.string.stream_favorited_activity_text_pattern);
    }
}
