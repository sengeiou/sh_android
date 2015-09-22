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

public class NiceShotViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.niced_shot_activity) String nicedShotText;
    @BindString(R.string.niced_shot_activity_with_comment) String nicedShotPrefixText;

    public NiceShotViewHolder(View view,
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
        return nicedShotText;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return nicedShotPrefixText;
    }
}
