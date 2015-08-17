package com.shootr.android.notifications.activity.nice;

import android.content.Context;
import com.shootr.android.R;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.activity.ActivityNotification;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;

public class NicedShotNotification extends ActivityNotification {

    private final String notificationContentText;
    private final String notificationTextPattern;

    @Inject
    public NicedShotNotification(Context context,
      NotificationBuilderFactory builderFactory,
      PicassoWrapper picasso,
      ActivityModel activityModel) {
        super(context, builderFactory, picasso, activityModel);
        this.notificationContentText = context.getString(R.string.notification_nice_content_text);
        this.notificationTextPattern = context.getString(R.string.notification_nice_text_pattern);
    }

    @Override
    public String getTitle() {
        return getActivity().getUsername();
    }

    @Override
    public String getContentText() {
        return notificationContentText;
    }

    @Override
    protected CharSequence getTickerText() {
        return String.format(notificationTextPattern, getActivity().getUsername());
    }
}
