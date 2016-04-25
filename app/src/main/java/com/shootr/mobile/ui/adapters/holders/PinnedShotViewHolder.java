package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class PinnedShotViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.pinned_shot_activity) String pinnedShotPattern;
    @BindString(R.string.pinned_shot_activity_with_comment) String pinnedShotPrefixPattern;

    public PinnedShotViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return pinnedShotPattern;
    }

    @Override protected String getActivityCommentPrefix(ActivityModel activity) {
        return pinnedShotPrefixPattern;
    }
}
