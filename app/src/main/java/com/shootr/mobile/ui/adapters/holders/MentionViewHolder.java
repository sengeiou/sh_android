package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class MentionViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.mentioned_shot_activity_with_comment) String mentionedPrefixText;

    public MentionViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        throw new IllegalStateException("Can't receive a mention without comment!! You mad bro?");
    }

    @Override protected String getActivityCommentPrefix(ActivityModel activity) {
        return mentionedPrefixText;
    }
}
