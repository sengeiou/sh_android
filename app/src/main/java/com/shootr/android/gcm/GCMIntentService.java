package com.shootr.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.gcm.event.RequestWatchByPushEvent;
import com.shootr.android.gcm.notifications.ShootrNotificationManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.util.Date;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class GCMIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;
    private static final int PUSH_TYPE_START_MATCH = 3;
    private static final int PUSH_TYPE_WATCH_REQUEST = 4;

    @Inject ShootrNotificationManager notificationManager;
    @Inject UserManager userManager;
    @Inject ShootrService service;
    @Inject ShotModelMapper shotModelMapper;
    @Inject WatchManager watchManager;
    @Inject MatchManager matchManager;
    @Inject UserWatchingModelMapper userWatchingModelMapper;
    @Inject UserModelMapper userModelMapper;
    @Inject MatchModelMapper matchModelMapper;
    @Inject Bus bus;

    public GCMIntentService() {
        super("GCM Service");
    }

    private static final String ID_USER = "idUser";
    private static final String ID_SHOT = "idShot";
    private static final String ID_MATCH = "idMatch";
    private static final String STATUS = "status";
    private static final String PLACE = "place";

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
                case PUSH_TYPE_START_MATCH:
                    receivedStartMatch(parameters);
                    break;
                case PUSH_TYPE_WATCH_REQUEST:
                    receivedWatchRequest(parameters);
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

    private void receivedStartMatch(JSONObject parameters) throws JSONException, IOException {
        Long idMatch = parameters.getLong(ID_MATCH);
        MatchEntity match = service.getMatchByIdMatch(idMatch);
        String text = match.getLocalTeamName()+"-"+match.getVisitorTeamName();
        notificationManager.sendMatchStartedNotification(text);
    }

    private void receivedShot(JSONObject parameters) throws JSONException, IOException {
        Long idShot = parameters.getLong(ID_SHOT);
        Long idUser = parameters.getLong(ID_USER);
        ShotEntity shot = service.getShotById(idShot);
        UserEntity user = userManager.getUserByIdUser(idUser);
        if (shot != null && user != null) {
            ShotModel shotModel = shotModelMapper.toShotModel(user, shot);
            notificationManager.sendNewShotNotification(shotModel);
        } else {
            Timber.e("Shot or User null received, can't show notifications :(");
        }
    }

    private void receivedWatchRequest(JSONObject parameters) throws JSONException, IOException {
        Long idUser = parameters.getLong(ID_USER);
        Long idMatch = parameters.getLong(ID_MATCH);
        Long status = parameters.getLong(STATUS);
        String place = parameters.optString(PLACE);
        if ("null".equals(place) || place.isEmpty()) {
            place = null;
        }

        MatchEntity matchEntity = service.getMatchByIdMatch(idMatch);
        UserEntity userFromNotification = userManager.getUserByIdUser(idUser);

        boolean isWatching = WatchEntity.STATUS_WATCHING.equals(status);
        boolean isLive = matchEntity.getStatus().equals(MatchEntity.STARTED);

        UserWatchingModel userWatchingModel = userWatchingModelMapper.toUserWatchingModel(userFromNotification, isWatching,
          isLive, place);
        MatchModel matchModel = matchModelMapper.toMatchModel(matchEntity);

        notificationManager.sendWatchRequestNotification(userWatchingModel, matchModel);

        retrieveAndStoreNewWatch(userFromNotification, matchEntity);
    }

    private void retrieveAndStoreNewWatch(UserEntity userEntity, MatchEntity matchEntity) throws IOException {
        WatchEntity watchEntity = service.getWatchStatus(userEntity.getIdUser(), matchEntity.getIdMatch());

        matchManager.saveMatch(matchEntity);
        userManager.saveUser(userEntity);
        watchManager.createUpdateWatch(watchEntity);

        bus.post(new RequestWatchByPushEvent());
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
