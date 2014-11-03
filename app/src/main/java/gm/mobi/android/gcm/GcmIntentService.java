package gm.mobi.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.gcm.notifications.BagdadNotificationManager;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.ui.model.ShotModel;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.ShotModelMapper;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class GcmIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;
    private static final int PUSH_TYPE_START_MATCH = 3;

    @Inject BagdadNotificationManager notificationManager;
    @Inject SQLiteOpenHelper openHelper;
    @Inject UserManager userManager;
    @Inject BagdadService service;
    @Inject ShotModelMapper shotModelMapper;
    @Inject UserModelMapper userModelMapper;
    private SQLiteDatabase database;

    public GcmIntentService() {
        super("GCM Service");
    }

    @Override public void onCreate() {
        super.onCreate();
        GolesApplication.get(this).inject(this);
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
                case PUSH_TYPE_START_MATCH:
                    receivedStartMatch(text);
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

    private void receivedStartMatch(String text){
        notificationManager.sendMatchStartedNotification(text);
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        Long idShot = parameters.getLong("idShot");
        Long idUser = parameters.getLong("idUser");
        ShotEntity shot = service.getShotById(idShot);
        UserEntity user = userManager.getUserByIdUser(idUser);
        if (shot != null && user != null) {
            ShotModel shotModel = shotModelMapper.toShotModel(user, shot);
            notificationManager.sendNewShotNotification(shotModel);
        } else {
            Timber.e("Shot or User null received, can't show notifications :(");
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
