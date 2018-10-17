package com.shootr.mobile.notifications.message;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.util.ImageLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MultipleMessageNotification extends AbstractMessageNotification {

  public static final Bitmap MULTIPLE_ICONS_BITMAP = null;
  private static final int REQUEST_OPEN = 2;

  private final List<PrivateMessageActivityNotification> individualNotifications;
  private final ImageLoader imageLoader;

  public MultipleMessageNotification(Context context, ImageLoader imageLoader,
      NotificationBuilderFactory builderFactory, List<PrivateMessageActivityNotification> individualNotifications) {
    super(context, builderFactory);
    this.imageLoader = imageLoader;
    this.individualNotifications = individualNotifications;
  }

  @Override public void setNotificationValues(NotificationCompat.Builder builder,
      Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentTitle(getTitle());
    builder.setContentText(getCollapsedContent());
    builder.setStyle(getInboxStyleFromActivities());
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
    if (!hasMoreThanOneIcon()) {
      builder.setContentIntent(getSingleMessageNotificationPendingIntent(individualNotifications.get(0).getIdUser()));
    }
  }

  private NotificationCompat.Style getInboxStyleFromActivities() {
    NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
    for (PrivateMessageActivityNotification notification : individualNotifications) {
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
    for (PrivateMessageActivityNotification notification : individualNotifications) {
      String title = notification.getTitle();
      names.add(title);
    }
    return names;
  }

  private CharSequence getTitle() {
    return getResources().getString(R.string.new_messages_notification, individualNotifications.size());
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
    Iterator<PrivateMessageActivityNotification> notificationsIterator = individualNotifications.iterator();
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

  private PendingIntent getSingleMessageNotificationPendingIntent(String idUser) {
    Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
        NotificationIntentReceiver.ACTION_OPEN_PRIVATE_MESSAGE);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER, idUser);
    return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }
}
