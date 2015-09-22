package com.shootr.android.ui.adapters;

import android.view.View;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;

public class MentionViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.mentioned_shot_activity) String mentionedText;
    @BindString(R.string.mentioned_shot_activity_with_comment) String mentionedPrefixText;

    public MentionViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener) {
        super(view,
          imageLoader,
          androidTimeUtils,
          shotTextSpannableBuilder,
          onAvatarClickListener,
          onUsernameClickListener);
    }

    @Override
    protected String getActivitySimpleComment(ActivityModel activity) {
        return mentionedText;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return mentionedPrefixText;
    }
}
