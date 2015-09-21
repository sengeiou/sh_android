package com.shootr.android.ui.adapters;

import android.text.SpannableStringBuilder;
import android.view.View;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

public class ActivityMentionViewHolder extends ActivityShotViewHolder {

    public ActivityMentionViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      ShotTextSpannableBuilder shotTextSpannableBuilder) {
        super(view,
          avatarClickListener,
          videoClickListener,
          onNiceShotListener,
          onUsernameClickListener,
          timeUtils,
          imageLoader,
          shotTextSpannableBuilder);
    }

    @Override public SpannableStringBuilder createComment(ShotModel item, String comment, String tag) {
        SpannableStringBuilder commentWithTag;
        if (comment == null) {
            String resultComment = mentionedShot;
            commentWithTag = buildCommentTextWithTag(resultComment, tag);
        } else {
            String resultComment = String.format(mentionedShotWithComment, item.getUsername());
            commentWithTag = buildCommentTextWithTag(resultComment, comment);
        }
        return commentWithTag;
    }
}
