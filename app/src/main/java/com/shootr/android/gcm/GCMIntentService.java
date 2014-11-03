package com.shootr.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import com.shootr.android.ShootrApplication;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.gcm.notifications.ShootrNotificationManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class GCMIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;

    @Inject ShootrNotificationManager notificationManager;
    @Inject SQLiteOpenHelper openHelper;
    @Inject UserManager userManager;
    @Inject ShootrService service;
    @Inject ShotModelMapper shotModelMapper;
    @Inject UserModelMapper userModelMapper;
    private SQLiteDatabase database;

    public GCMIntentService() {
        super("GCM Service");
    }

    @Override public void onCreate() {
        super.onCreate();
        ShootrApplication.get(this).inject(this);
        database = openHelper.getReadableDatabase();
        userManager.setDataBase(database);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        database.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        Long idShot = parameters.getLong("idShot");
        Long idUser = parameters.getLong("idUser");
        ShotEntity shot = service.getShotById(idShot);
        UserEntity user = userManager.getUserByIdUser(idUser);
        if (shot != null && user != null) {
            ShotModel shotVO = shotModelMapper.toShotModel(user, shot);
            notificationManager.sendNewShotNotification(shotVO);
        } else {
            Timber.e("Shot or User null recived, can't show notifications :(");
        }
    }

    private void receivedFollow(JSONObject parameters) throws JSONException, IOException {
        Long idUser = parameters.getLong("idUser");
        UserEntity user = service.getUserByIdUser(idUser);
        UserModel userModel = userModelMapper.toUserModel(user, null, false);
        notificationManager.sendNewFollowerNotification(userModel);
    }

    private void receivedUnknown(JSONObject parameters) {
        Timber.e("Received unknown notification with parameters: %s", parameters.toString());
    }

}
