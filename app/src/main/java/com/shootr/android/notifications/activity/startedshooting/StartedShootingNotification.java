package com.shootr.android.notifications.activity.startedshooting;

import android.content.Context;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.activity.ActivityNotification;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;

public class StartedShootingNotification extends ActivityNotification {

    public StartedShootingNotification(Context context,
      NotificationBuilderFactory builderFactory,
      PicassoWrapper picasso,
      ActivityModel activityModel) {
        super(context, builderFactory, picasso, activityModel);
    }

    @Override
    public String getTitle() {
        return getActivity().getUsername();
    }

    @Override
    public String getContentText() {
        return getActivity().getComment();
    }

    @Override
    protected CharSequence getTickerText() {
        return getActivity().getUsername() + ": " + getActivity().getComment();
    }
}
