package com.shootr.android.notifications.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.shootr.android.R;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MultipleActivityNotification extends AbstractActivityNotification {

    private final List<SingleActivityNotification> individualNotifications;

    public MultipleActivityNotification(Context context,
      NotificationBuilderFactory builderFactory,
      List<SingleActivityNotification> individualNotifications) {
        super(context, builderFactory);
        this.individualNotifications = individualNotifications;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentTitle(getTitle());
        builder.setContentText(getCollapsedContent());
        builder.setStyle(getInboxStyleFromActivities());
    }

    private NotificationCompat.Style getInboxStyleFromActivities() {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        for (SingleActivityNotification notification: individualNotifications) {
            Spannable styledLine = getSpannableLineFromTitleAndText(notification.getTitle(),
              notification.getContentText());
            inbox.addLine(styledLine);
        }
        return inbox;
    }

    private Spannable getSpannableLineFromTitleAndText(String title, String contentText) {
        Spannable styledLine = new SpannableString(String.format("%s %s", title, contentText));
        int nameEndIndex = title.length();
        styledLine.setSpan(new StyleSpan(Typeface.BOLD), 0, nameEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledLine;
    }

    private CharSequence getCollapsedContent() {
        Set<String> userNames = getUserNamesFromActivities();
        return TextUtils.join(", ", userNames);
    }

    private Set<String> getUserNamesFromActivities() {
        Set<String> names = new TreeSet<>();
        for (SingleActivityNotification notification : individualNotifications) {
            String userName = notification.getActivity().getUsername();
            names.add(userName);
        }
        return names;
    }

    private CharSequence getTitle() {
        return getResources().getString(R.string.notification_activity_multiple_title, individualNotifications.size());
    }

    @Override
    protected CharSequence getTickerText() {
        return getTitle();
    }

    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    public Bitmap getWearBackground() {
        return null;
    }
}
