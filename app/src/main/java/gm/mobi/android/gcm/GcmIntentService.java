package gm.mobi.android.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Style;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;

import gm.mobi.android.ui.model.ShotVO;
import gm.mobi.android.ui.model.UserVO;
import gm.mobi.android.ui.model.mappers.ShotVOMapper;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
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

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;

    @Inject BagdadNotificationManager notificationManager;
    @Inject SQLiteOpenHelper openHelper;
    @Inject UserManager userManager;
    @Inject BagdadService service;
    @Inject ShotVOMapper shotVOMapper;

    public GcmIntentService() {
        super("GCM Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GolesApplication.get(this).inject(this);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        userManager.setDataBase(db);

        Bundle extras = intent.getExtras();
        String text = extras.getString("t");
        Timber.d("Received notification intent: %s", text);
        try {
            JSONObject parameters = new JSONObject(extras.getString("p"));
            int pushType = parameters.getInt("pushType");

            switch (pushType) {
                case PUSH_TYPE_SHOT:
                    receivedShot(parameters);
                    break;
                case PUSH_TYPE_FOLLOW:
                    receivedFollow(parameters);
                    break;
                default:
                    receivedUnknown(parameters);
            }
        } catch (JSONException e) {
            Timber.e(e, "Error parsing notification parameters");
            notificationManager.sendErrorNotification(e.getMessage());
        } catch (Exception e) {
            Timber.e(e, "Error creating notification");
            notificationManager.sendErrorNotification(e.getMessage());
        }
        db.close();
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        Long idShot = parameters.getLong("idShot");
        Long idUser = parameters.getLong("idUser");
        Shot shot = service.getShotById(idShot);
        User user = userManager.getUserByIdUser(idUser);
        ShotVO shotVO = shotVOMapper.toVO(user, null, shot, 0L);
        notificationManager.sendNewShotNotification(shotVO);
    }

    private void receivedFollow(JSONObject parameters) throws JSONException, IOException {
        Long idUser = parameters.getLong("idUser");
        User user = service.getUserByIdUser(idUser);
        notificationManager.sendNewFollowerNotification(user);
    }

    private void receivedUnknown(JSONObject parameters) {
        Timber.e("Received unknown notification with parameters: %s", parameters.toString());
    }

}
