package com.shootr.mobile.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.ActivityType;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.bus.BadgeChanged;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.notifications.activity.ActivityNotificationManager;
import com.shootr.mobile.notifications.shot.ShotNotificationManager;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.sloydev.jsonadapters.JsonAdapter;

import org.json.JSONException;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class GCMIntentService extends IntentService {

    private static final String EXTRA_NOTIFICATION_VALUES = "t";
    private static final String EXTRA_PARAMETERS = "p";
    private static final String EXTRA_BADGE_INCREMENT = "b";
    private static final String EXTRA_SILENT = "s";

    @Inject ShotNotificationManager shotNotificationManager;

    @Inject ActivityNotificationManager activityNotificationManager;
    @Inject @Remote ShotRepository remoteShotRepository;
    @Inject ShotModelMapper shotModelMapper;
    @Inject JsonAdapter jsonAdapter;
    @Inject @ActivityBadgeCount IntPreference badgeCount;
    @Inject BusPublisher busPublisher;

    public GCMIntentService() {
        super("GCM Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ShootrApplication.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Timber.d("Received notification intent: %s", extras.toString());

        try {
            PushNotification push = buildPushNotificationFromExtras(extras);

            if (push.getBadgeIncrement() > 0) {
                badgeCount.set(badgeCount.get() + push.getBadgeIncrement());
                busPublisher.post(new BadgeChanged.Event());
            }

            if (push.isSilent()) {
                return;
            }

            switch (push.getParameters().getPushType()) {
                case PushNotification.Parameters.PUSH_TYPE_SHOT:
                    receivedShot(push);
                    break;
                case PushNotification.Parameters.PUSH_TYPE_ACTIVITY:
                    receivedActivity(push);
                    break;
                case PushNotification.Parameters.PUSH_TYPE_STREAM:
                    receivedStream(push);
                    break;
                default:
                    receivedUnknown(push);
            }
        } catch (JSONException e) {
            Timber.e(e, "Error parsing notification parameters: %s", extras);
        } catch (Exception e) {
            Timber.e(e, "Error creating notification");
        }
    }

    private PushNotification buildPushNotificationFromExtras(Bundle extras) throws IOException {
        String serializedValues = extras.getString(EXTRA_NOTIFICATION_VALUES);
        String serializedParameters = extras.getString(EXTRA_PARAMETERS);
        String badgeText = checkNotNull(extras.getString(EXTRA_BADGE_INCREMENT));
        Integer badge = Integer.valueOf(badgeText);
        String silentText = checkNotNull(extras.getString(EXTRA_SILENT));
        Boolean silent = Boolean.valueOf(silentText);

        PushNotification.Parameters parameters = jsonAdapter.fromJson(serializedParameters,
          PushNotification.Parameters.class);
        PushNotification.NotificationValues values = null;
        if (serializedValues != null) {
            values = jsonAdapter.fromJson(serializedValues, PushNotification.NotificationValues.class);
        }

        return new PushNotification(values, parameters, silent, badge);
    }

    private void receivedStream(PushNotification pushNotification) throws JSONException, IOException {
        setupGoToStreamTimelineNotification(pushNotification);
    }

    private void receivedShot(PushNotification pushNotification) throws JSONException, IOException {
        String idShot = pushNotification.getParameters().getIdShot();
        Shot shot = remoteShotRepository.getShot(idShot);
        ShotModel shotModel = shotModelMapper.transform(shot);
        shotNotificationManager.sendNewShotNotification(shotModel);
    }

    private void receivedActivity(PushNotification push) throws JSONException {
        String activityType = checkNotNull(push.getParameters().getActivityType());
        switch (activityType) {
            case ActivityType.START_FOLLOW:
                setupGoToProfileNotification(push);
                break;
            case ActivityType.STREAM_FAVORITED:
            case ActivityType.CHECKIN:
            case ActivityType.SHARE_STREAM:
            case ActivityType.OPENED_STREAM:
                setupGoToStreamTimelineNotification(push);
                break;
            case ActivityType.NICE_SHOT:
            case ActivityType.SHARE_SHOT:
            case ActivityType.MENTION:
            case ActivityType.REPLY_SHOT:
            case ActivityType.STARTED_SHOOTING:
                setupGoToShotDetailNotification(push);
                break;
            default:
                Timber.w("Received unknown activity type: %s", activityType);
        }
    }

    private void setupGoToProfileNotification(PushNotification push) {
        String idUser = push.getParameters().getIdUser();
        activityNotificationManager.sendFollowNotification(push.getNotificationValues(), checkNotNull(idUser));
    }

    private void setupGoToStreamTimelineNotification(PushNotification push) {
        String idStream = push.getParameters().getIdStream();
        String title = push.getParameters().getTitle();
        String idStreamHolder = push.getParameters().getIdStreamHolder();
        activityNotificationManager.sendOpenStreamNotification(push.getNotificationValues(),
          checkNotNull(idStream), checkNotNull(idStreamHolder), checkNotNull(title));
    }

    private void setupGoToShotDetailNotification(PushNotification push) {
        String idShot = push.getParameters().getIdShot();
        activityNotificationManager.sendOpenShotDetailNotification(push.getNotificationValues(), checkNotNull(idShot));
    }

    private void receivedUnknown(PushNotification pushNotification) {
        Timber.w("Received unknown notification: %s", pushNotification.toString());
    }
}
