package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;

public class NiceShotViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.niced_shot_activity) String nicedShotText;
    @BindString(R.string.niced_shot_activity_with_comment) String nicedShotPrefixText;

    public NiceShotViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override
    protected String getActivitySimpleComment(ActivityModel activity) {
        return nicedShotText;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return nicedShotPrefixText;
    }
}
