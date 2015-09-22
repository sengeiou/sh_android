package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;

public class ShareShotViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.shared_shot_activity) String sharedShotPattern;
    @BindString(R.string.shared_shot_activity_with_comment) String sharedShotPrefixPattern;

    public ShareShotViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override
    protected String getActivitySimpleComment(ActivityModel activity) {
        return String.format(sharedShotPattern, activity.getShot().getUsername());
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return String.format(sharedShotPrefixPattern, activity.getShot().getUsername());
    }
}
