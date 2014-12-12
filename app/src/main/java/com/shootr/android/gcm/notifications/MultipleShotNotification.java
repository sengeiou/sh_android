package com.shootr.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleShotNotification extends AbstractShotNotification {

    private List<ShotModel> shots;

    public MultipleShotNotification(Context context, NotificationBuilderFactory builderFactory, List<ShotModel> shots) {
        super(context, builderFactory);
        this.shots = shots;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentTitle(getTitle());
        builder.setContentText(getCollapsedContent());
        builder.setStyle(getInboxStyleFromShots());
    }

    private String getCollapsedContent() {
        List<String> userNames = getUserNamesFromShots();
        //TODO discard repeated
        return TextUtils.join(", ", userNames);
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }

    public String getTitle() {
        return getResources().getString(R.string.notification_shot_multiple_title, shots.size());
    }

    protected NotificationCompat.InboxStyle getInboxStyleFromShots() {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        for (ShotModel shot : shots) {
            String userName = shot.getUsername();
            String shotText = getShotText(shot);
            Spannable styledLine = getSpannableLineFromNameAndComment(userName, shotText);
            inbox.addLine(styledLine);
        }
        return inbox;
    }

    protected Spannable getSpannableLineFromNameAndComment(String name, String comment) {
        Spannable styledLine = new SpannableString(String.format("%s %s", name, comment));
        int nameEndIndex = name.length();
        styledLine.setSpan(new StyleSpan(Typeface.BOLD), 0, nameEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledLine;
    }

    private List<String> getUserNamesFromShots() {
        List<String> names = new ArrayList<>(shots.size());
        for (ShotModel shot : shots) {
            String userName = shot.getUsername();
            names.add(userName);
        }
        Collections.sort(names);
        return names;
    }
}
