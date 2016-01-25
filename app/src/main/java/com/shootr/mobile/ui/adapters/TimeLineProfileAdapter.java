package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class TimeLineProfileAdapter extends TimelineAdapter {

    private final OnHideClickListener onHideClickListener;

    public TimeLineProfileAdapter(Context context, ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnHideClickListener onHideClickListener, OnUsernameClickListener onUsernameClickListener) {
        super(context,
          imageLoader,
          timeUtils,
          avatarClickListener,
          videoClickListener,
          null,
          onUsernameClickListener);
        this.onHideClickListener= onHideClickListener;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = null;
        switch (getItemViewType(position)) {
            case 0: // Shot
                view = inflater.inflate(R.layout.item_list_shot_profile, container, false);
                view.setTag(new ShotProfileViewHolder(view, getAvatarClickListener(), getVideoClickListener(),
                  onHideClickListener,
                  getOnUsernameClickListener(),
                  getTimeUtils(),
                  getImageLoader(),
                  getShotTextSpannableBuilder()));
                break;
            default:
                break;
        }
        return view;
    }
}
