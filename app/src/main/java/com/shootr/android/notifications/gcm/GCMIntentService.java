package com.shootr.android.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.notifications.follow.FollowNotificationManager;
import com.shootr.android.notifications.shot.ShotNotificationManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.ShotEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class GCMIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;

    @Inject ShotNotificationManager shotNotificationManager;
    @Inject FollowNotificationManager followNotificationManager;
    @Inject UserManager userManager;
    @Inject ShootrService service;
    @Inject @Local UserRepository localUserRepository;
    @Inject ShotEntityModelMapper shotEntityModelMapper;
    @Inject UserEntityModelMapper userEntityModelMapper;
    @Inject UserModelMapper userModelMapper;

    public GCMIntentService() {
        super("GCM Service");
    }

    private static final String ID_USER = "idUser";
    private static final String ID_SHOT = "idShot";
    private static final String ID_EVENT = "idEvent";

    @Override public void onCreate() {
        super.onCreate();
        ShootrApplication.get(this).inject(this);
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
        } catch (Exception e) {
            Timber.e(e, "Error creating notification");
        }
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        String idShot = parameters.getString(ID_SHOT);
        String idUser = parameters.getString(ID_USER);
        ShotEntity shot = service.getShotById(idShot);
        UserEntity user = userManager.getUserByIdUser(idUser);
        if (shot != null && user != null) {
            ShotModel shotModel = shotEntityModelMapper.toShotModel(user, shot);
            shotNotificationManager.sendNewShotNotification(shotModel);
        } else {
            Timber.e("Shot or User null received, can't show notifications :(");
        }
    }

    private void receivedFollow(JSONObject parameters) throws JSONException, IOException {
        String idUser = parameters.getString("idUser");
        UserEntity user = service.getUserByIdUser(idUser);
        UserModel userModel = userEntityModelMapper.toUserModel(user, null, false);
        followNotificationManager.sendNewFollowerNotification(userModel);
    }

    private void receivedUnknown(JSONObject parameters) {
        Timber.e("Received unknown notification with parameters: %s", parameters.toString());
    }

}
