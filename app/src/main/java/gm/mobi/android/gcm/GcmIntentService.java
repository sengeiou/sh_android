package gm.mobi.android.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Style;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.gcm.notifications.BagdadNotificationManager;
import gm.mobi.android.service.BagdadService;
import timber.log.Timber;

public class GcmIntentService extends IntentService {

    @Inject BagdadNotificationManager notificationManager;
    @Inject SQLiteOpenHelper openHelper;
    @Inject UserManager userManager;
    @Inject BagdadService service;

    public GcmIntentService() {
        super("GCM Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GolesApplication.get(this).inject(this);
        userManager.setDataBase(openHelper.getReadableDatabase());

        Bundle extras = intent.getExtras();
        try {
            String text = extras.getString("t");
            JSONObject parameters = new JSONObject(extras.getString("p"));
            Long idShot = parameters.getLong("idShot");
            Long idUser = parameters.getLong("idUser");
            Shot shot = service.getShotById(idShot);
            User user = userManager.getUserByIdUser(idUser);
            shot.setUser(user);
            notificationManager.sendNewShotNotification(shot);
        } catch (JSONException | IOException e) {
            Timber.e(e, "Error creating notification");
        } catch (Exception e) {
            Timber.e(e, "Error creating notification");
            notificationManager.sendErrorNotification(e.getMessage());
        }
    }
}
