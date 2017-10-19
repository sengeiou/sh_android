package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.StreamTitleSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class MentionViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.mentioned_shot_activity_with_comment) String mentionedPrefixText;

    public MentionViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
        OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener,
        OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener,
            onStreamTitleClickListener);
    }

    @Override protected CharSequence getTitle(ActivityModel activity) {

        StreamTitleSpan streamTitleSpan =
            new StreamTitleSpan(activity.getIdStream(), activity.getStreamTitle(),
                activity.getIdStreamAuthor()) {
                @Override
                public void onStreamClick(String streamId, String streamTitle, String idAuthor) {
                    onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle, idAuthor);
                }
            };

        return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
            .append(activity.getUsername())
            .popSpan()
            .append(getActivitySimpleComment(activity))
            .append(" ")
            .pushSpan(new StyleSpan(Typeface.BOLD))
            .pushSpan(streamTitleSpan)
            .append(activity.getStreamTitle())
            .popSpan()
            .build();
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return mentionedPrefixText;
    }

    @Override protected String getActivityCommentPrefix(ActivityModel activity) {
        return mentionedPrefixText;
    }
}
