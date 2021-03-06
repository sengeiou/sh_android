package com.shootr.mobile.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.R;

public abstract class CommonNotification {

    private static final Uri RINGTONE_DEFAULT = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private NotificationBuilderFactory builderFactory;
    private Resources resources;
    private Context context;
    private NotificationManager notificationManager;

    public CommonNotification(Context context, NotificationBuilderFactory builderFactory) {
        this.context = context;
        this.builderFactory = builderFactory;
        resources = context.getResources();
    }

    private void setCommonNotificationValues(NotificationCompat.Builder builder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notifManager = getManager();
            NotificationChannel channel = notifManager.getNotificationChannel("canalComunicacion1");
            if (channel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                channel = new NotificationChannel("canalComunicacion1", "canal1", importance);
                channel.enableVibration(false);
            }
            notifManager.createNotificationChannel(channel);
        }

        int primaryColor = resources.getColor(R.color.primary);
        builder.setSound(getRingtone());
        builder.setPriority(getPriority());
        builder.setSmallIcon(getSmallIcon());
        builder.setAutoCancel(true);
        builder.setTicker(getTickerText());
        builder.setColor(primaryColor);
        builder.setCategory(NotificationCompat.CATEGORY_SOCIAL);
        builder.setLights(primaryColor, 1000, 3000);
        Bitmap largeIcon = getLargeIcon();
        builder.setLargeIcon(shouldRoundIcon() ? transformCircularBitmap(largeIcon) : largeIcon);
        builder.extend(new NotificationCompat.WearableExtender().setBackground(largeIcon));
        builder.setChannelId("canalComunicacion1");

    }

    protected abstract CharSequence getTickerText();

    public abstract void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown);

    public Notification build() {
        NotificationCompat.Builder builder = builderFactory.getNotificationBuilder(context);
        setCommonNotificationValues(builder);
        setNotificationValues(builder, true);
        return builder.build();
    }

    protected Resources getResources() {
        return resources;
    }

    protected Context getContext() {
        return context;
    }

    public Uri getRingtone() {
        return RINGTONE_DEFAULT;
    }

    public int getPriority() {
        return NotificationCompat.PRIORITY_HIGH;
    }

    @DrawableRes public int getSmallIcon() {
        return R.drawable.ic_stat_app_icon;
    }

    public abstract Bitmap getLargeIcon();

    private boolean shouldRoundIcon() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * WARNING: This method doesn't recycle the input, in case it needs reusing
     */
    private Bitmap transformCircularBitmap(Bitmap input) {
        if (input == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        BitmapShader shader = new BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        final RectF rect = new RectF(0f, 0f, input.getWidth(), input.getHeight());

        canvas.drawOval(rect, paint);

        return output;
    }

  private NotificationManager getManager() {
    if (notificationManager == null) {
      notificationManager =
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    return notificationManager;
  }
}
