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

public class ReplyViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.replied_activity_with_comment) String repliedWithCommentPrefixText;
    @BindString(R.string.replied_activity) String repliedWithoutCommentPrefixText;

    public ReplyViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override protected CharSequence getTitle(ActivityModel activity) {
        return new Truss()
            .pushSpan(new StyleSpan(Typeface.BOLD))
            .append(activity.getUsername()).popSpan()
            .append(getActivitySimpleComment(activity)).append(" ")
            .pushSpan(new StyleSpan(Typeface.BOLD))
            .append(activity.getStreamTitle()).popSpan().build();
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return repliedWithoutCommentPrefixText;
    }

    @Override protected String getActivityCommentPrefix(ActivityModel activity) {
        return repliedWithCommentPrefixText;
    }
}
