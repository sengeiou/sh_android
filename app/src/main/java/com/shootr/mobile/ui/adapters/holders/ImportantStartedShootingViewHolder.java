package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class ImportantStartedShootingViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.important_started_shooting_activity_notext_pattern) String startedShootingPattern;
    @BindString(R.string.important_started_shooting_activity_text_pattern) String startedShootingPatternWithComment;

    public ImportantStartedShootingViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
        OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClick);
    }

    @Override protected CharSequence getTitle(ActivityModel activity) {

            return new Truss()
                .pushSpan(new StyleSpan(Typeface.BOLD))
                .append("Admin. ").popSpan()
                .pushSpan(new StyleSpan(Typeface.BOLD))
                .append(activity.getUsername()).popSpan()
                .append(getActivitySimpleComment(activity)).append(" ")
                .pushSpan(new StyleSpan(Typeface.BOLD))
                .append(activity.getUsername()).popSpan().build();

    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return startedShootingPattern;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return startedShootingPatternWithComment;
    }
}
