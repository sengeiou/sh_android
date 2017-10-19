package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class StartedShootingViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.started_shooting_activity_text_pattern) String startedShootingPattern;
    @BindString(R.string.started_shooting_activity_text_pattern_with_comment) String startedShootingPatternWithComment;

    public StartedShootingViewHolder(View view, ImageLoader imageLoader,
        AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
        OnShotClick onShotClickListener, OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener,
            onStreamTitleClickListener);
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return startedShootingPattern;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return startedShootingPatternWithComment;
    }

}
