package gm.mobi.android.gcm.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.gcm.NotificationIntentReceiver;
import com.shootr.android.gcm.notifications.CommonNotification;
import com.shootr.android.gcm.notifications.NotificationBuilderFactory;

public class StartMatchNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 2;
    String text;
    public StartMatchNotification(Context context, NotificationBuilderFactory builderFactory, String text) {
        super(context, builderFactory);
        this.text = text;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(text);
        String message = getResources().getText(R.string.enter_to_confirm).toString();
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentIntent(getStartMatchNotificationPendingIntent());
    }

    protected PendingIntent getStartMatchNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_START_MATCH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),REQUEST_OPEN,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }


    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }
}