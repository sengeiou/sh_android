package com.shootr.android.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.notifications.ShootrNotificationManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.EventEntityModelMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class GCMIntentService extends IntentService {

    private static final int PUSH_TYPE_SHOT = 1;
    private static final int PUSH_TYPE_FOLLOW = 2;
    private static final int PUSH_TYPE_START_EVENT = 3;
    private static final int PUSH_TYPE_WATCH_REQUEST = 4;

    @Inject ShootrNotificationManager notificationManager;
    @Inject UserManager userManager;
    @Inject ShootrService service;
    @Inject ShotModelMapper shotModelMapper;
    @Inject WatchManager watchManager;
    @Inject EventManager eventManager;
    @Inject UserEntityWatchingModelMapper userWatchingModelMapper;
    @Inject UserEntityModelMapper userModelMapper;
    @Inject EventEntityModelMapper eventEntityModelMapper;
    @Inject BusPublisher busPublisher;

    public GCMIntentService() {
        super("GCM Service");
    }

    private static final String ID_USER = "idUser";
    private static final String ID_SHOT = "idShot";
    private static final String ID_EVENT = "idEvent";
    private static final String STATUS = "status";

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
                case PUSH_TYPE_START_EVENT:
                    receivedStartEvent(parameters);
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

    private void receivedStartEvent(JSONObject parameters) throws JSONException, IOException {
        Long idEvent = parameters.getLong(ID_EVENT);
        EventEntity eventEntity = service.getEventById(idEvent);
        String text = eventEntity.getTitle();
        notificationManager.sendEventStartedNotification(text);
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
        Long idEvent = parameters.getLong(ID_EVENT);
        String place = parameters.optString(STATUS);
        if ("null".equals(place) || place.isEmpty()) {
            place = null;
        }

        EventEntity eventEntity = service.getEventById(idEvent);
        UserEntity userFromNotification = userManager.getUserByIdUser(idUser);

        UserWatchingModel userWatchingModel = userWatchingModelMapper.toUserWatchingModel(userFromNotification, place);
        EventModel eventModel = eventEntityModelMapper.toEventModel(eventEntity);

        notificationManager.sendWatchRequestNotification(userWatchingModel, eventModel);

        retrieveAndStoreNewWatch(userFromNotification, eventEntity);
    }

    private void retrieveAndStoreNewWatch(UserEntity userEntity, EventEntity eventEntity) throws IOException {
        WatchEntity watchEntity = service.getWatchStatus(userEntity.getIdUser(), eventEntity.getIdEvent());

        eventManager.saveEvent(eventEntity);
        userManager.saveUser(userEntity);
        watchManager.createUpdateWatch(watchEntity);

        busPublisher.post(new WatchUpdateRequest.Event());
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
