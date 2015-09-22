package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;

public class MentionViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.mentioned_shot_activity_with_comment) String mentionedPrefixText;

    public MentionViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override
    protected String getActivitySimpleComment(ActivityModel activity) {
        throw new IllegalStateException("Can't receive a mention without comment!! You mad bro?");
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return mentionedPrefixText;
    }
}
