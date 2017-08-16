package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class ImportantStartedShootingViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.important_started_shooting_activity_notext_pattern) String startedShootingPattern;
    @BindString(R.string.important_started_shooting_activity_text_pattern) String startedShootingPatternWithComment;

    public ImportantStartedShootingViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
        OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClick);
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return startedShootingPattern;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return startedShootingPatternWithComment;
    }
}
