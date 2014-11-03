package gm.mobi.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import gm.mobi.android.R;

public class StartMatchNotification extends CommonNotification {

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