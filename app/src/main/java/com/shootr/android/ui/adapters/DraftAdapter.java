package com.shootr.android.ui.adapters;

import android.content.Context;
import android.view.View;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;

public class DraftAdapter extends TimelineAdapter{

    public DraftAdapter(Context context, PicassoWrapper picasso, View.OnClickListener avatarClickListener,
      View.OnClickListener imageClickListener, AndroidTimeUtils timeUtils) {
        super(context, picasso, avatarClickListener, imageClickListener, timeUtils);
    }

    @Override protected boolean isDraft(int position) {
        return true;
    }
}
