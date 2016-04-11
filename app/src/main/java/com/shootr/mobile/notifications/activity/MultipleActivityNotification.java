package com.shootr.mobile.notifications.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.util.ImageLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MultipleActivityNotification extends AbstractActivityNotification {

    public static final Bitmap MULTIPLE_ICONS_BITMAP = null;

    private final List<SingleActivityNotification> individualNotifications;
    private final ImageLoader imageLoader;

    public MultipleActivityNotification(Context context, ImageLoader imageLoader,
      NotificationBuilderFactory builderFactory, List<SingleActivityNotification> individualNotifications) {
        super(context, builderFactory);
        this.imageLoader = imageLoader;
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
        for (SingleActivityNotification notification : individualNotifications) {
            Spannable styledLine =
              getSpannableLineFromTitleAndText(notification.getTitle(), notification.getContentText());
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
        Set<String> titles = getTitlesFromActivities();
        return TextUtils.join(", ", titles);
    }

    private Set<String> getTitlesFromActivities() {
        Set<String> names = new TreeSet<>();
        for (SingleActivityNotification notification : individualNotifications) {
            String title = notification.getTitle();
            names.add(title);
        }
        return names;
    }

    private CharSequence getTitle() {
        return getResources().getString(R.string.new_activity_notification);
    }

    @Override protected CharSequence getTickerText() {
        return getTitle();
    }

    @Override @Nullable public Bitmap getLargeIcon() {
        if (hasMoreThanOneIcon()) {
            return MULTIPLE_ICONS_BITMAP;
        } else {
            return individualNotifications.get(0).getLargeIcon();
        }
    }

    private boolean hasMoreThanOneIcon() {
        Iterator<SingleActivityNotification> notificationsIterator = individualNotifications.iterator();
        String firstIcon = notificationsIterator.next().getNotificationValues().getIcon();

        boolean hasMoreThanOneIcon = false;
        while (notificationsIterator.hasNext()) {
            String nextIcon = notificationsIterator.next().getNotificationValues().getIcon();
            boolean areIconsDifferent =
              (firstIcon != null && !firstIcon.equals(nextIcon)) || firstIcon == null && nextIcon != null;
            if (areIconsDifferent) {
                hasMoreThanOneIcon = true;
            }
        }
        return hasMoreThanOneIcon;
    }
}
