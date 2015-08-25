package com.shootr.android.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;

public class CheckinViewHolder extends ClickableStreamActivityViewHolder {

    public CheckinViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view,
          imageLoader,
          androidTimeUtils,
          shotTextSpannableBuilder,
          onAvatarClickListener, onUsernameClickListener, onStreamTitleClickListener);
    }

    @NonNull
    protected String getPatternText() {
        return getContext().getString(R.string.checkin_activity_text_pattern);
    }
}
