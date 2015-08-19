package com.shootr.android.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.notifications.activity.ActivityNotificationManager;
import com.shootr.android.notifications.follow.FollowNotificationManager;
import com.shootr.android.notifications.shot.ShotNotificationManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.ActivityModelMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class GCMIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;
    private static final int PUSH_TYPE_ACTIVITY = 3;
    @Inject ShotNotificationManager shotNotificationManager;

    @Inject FollowNotificationManager followNotificationManager;
    @Inject ActivityNotificationManager activityNotificationManager;
    @Inject UserManager userManager;
    @Inject ShootrService service;
    @Inject @Local UserRepository localUserRepository;
    @Inject UserEntityModelMapper userEntityModelMapper;
    @Inject UserModelMapper userModelMapper;
    @Inject @Remote ActivityRepository remoteActivityRepository;
    @Inject ActivityModelMapper activityModelMapper;
    @Inject @Remote ShotRepository remoteShotRepository;
    @Inject ShotModelMapper shotModelMapper;

    public GCMIntentService() {
        super("GCM Service");
    }

    private static final String ID_SHOT = "idShot";
    private static final String ID_ACTIVITY = "idActivity";

    @Override public void onCreate() {
        super.onCreate();
        ShootrApplication.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String text = extras.getString("t");
        String parametersText = extras.getString("p");
        Timber.d("Received notification intent: %s", text);
        try {
            JSONObject parameters = new JSONObject(parametersText);
            int pushType = parameters.getInt("pushType");
            switch (pushType) {
                case PUSH_TYPE_SHOT:
                    receivedShot(parameters);
                    break;
                case PUSH_TYPE_FOLLOW:
                    receivedFollow(parameters);
                    break;
                case PUSH_TYPE_ACTIVITY:
                    receivedActivity(parameters);
                    break;
                default:
                    receivedUnknown(parameters);
            }
        } catch (JSONException e) {
            Timber.e(e, "Error parsing notification parameters: %s", parametersText);
        } catch (Exception e) {
            Timber.e(e, "Error creating notification");
        }
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        String idShot = parameters.getString(ID_SHOT);
        Shot shot = remoteShotRepository.getShot(idShot);
        if (shot != null) {
            ShotModel shotModel = shotModelMapper.transform(shot);
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

    private void receivedActivity(JSONObject parameters) throws JSONException {
        String idActivity = parameters.getString(ID_ACTIVITY);
        Activity activity = remoteActivityRepository.getActivity(idActivity);

        switch (checkNotNull(activity.getType())) {
            case ActivityType.CHECKIN:
                activityNotificationManager.sendNewCheckinNotification(activityModelMapper.transform(activity));
                break;
            case ActivityType.STARTED_SHOOTING:
                activityNotificationManager.sendNewStartedShootingNotification(activityModelMapper.transform(activity));
                break;
            case ActivityType.NICE_SHOT:
                activityNotificationManager.sendNewNicedShotNotification(activityModelMapper.transform(activity));
                break;
            default:
                Timber.w("Received unknown activity type: %s", activity.getType());
        }
    }

    private void receivedUnknown(JSONObject parameters) {
        Timber.e("Received unknown notification with parameters: %s", parameters.toString());
    }
}
