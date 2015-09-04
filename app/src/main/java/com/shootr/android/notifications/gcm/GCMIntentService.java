package com.shootr.android.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.prefs.ActivityBadgeCount;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.BadgeChanged;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.notifications.activity.ActivityNotificationManager;
import com.shootr.android.notifications.shot.ShotNotificationManager;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import timber.log.Timber;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class GCMIntentService extends IntentService {

    private static final String EXTRA_NOTIFICATION_VALUES = "t";
    private static final String EXTRA_PARAMETERS = "p";
    private static final String EXTRA_BADGE_INCREMENT = "b";
    private static final String EXTRA_SILENT = "s";

    @Inject ShotNotificationManager shotNotificationManager;

    @Inject ActivityNotificationManager activityNotificationManager;
    @Inject @Remote ShotRepository remoteShotRepository;
    @Inject ShotModelMapper shotModelMapper;
    @Inject ObjectMapper objectMapper;
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

        PushNotification.Parameters parameters = objectMapper.readValue(serializedParameters,
          PushNotification.Parameters.class);
        PushNotification.NotificationValues values = null;
        if (serializedValues != null) {
            values = objectMapper.readValue(serializedValues, PushNotification.NotificationValues.class);
        }

        return new PushNotification(values, parameters, silent, badge);
    }

    private void receivedShot(PushNotification pushNotification) throws JSONException, IOException {
        String idShot = pushNotification.getParameters().getIdShot();
        Shot shot = remoteShotRepository.getShot(idShot);
        if (shot != null) {
            ShotModel shotModel = shotModelMapper.transform(shot);
            shotNotificationManager.sendNewShotNotification(shotModel);
        } else {
            Timber.e("Shot or User null received, can't show notifications :(");
        }
    }

    private void receivedActivity(PushNotification push) throws JSONException {
        String activityType = checkNotNull(push.getParameters().getActivityType());
        switch (activityType) {
            case ActivityType.CHECKIN:
            case ActivityType.STARTED_SHOOTING:
            case ActivityType.SHARE_STREAM:
            case ActivityType.SHARE_SHOT:
            case ActivityType.OPENED_STREAM:
            case ActivityType.NICE_SHOT:
                activityNotificationManager.sendGenericActivityNotification(push.getNotificationValues());
                break;
            case ActivityType.START_FOLLOW:
                String idUser = push.getParameters().getIdUser();
                activityNotificationManager.sendFollowNotification(push.getNotificationValues(), checkNotNull(idUser));
                break;
            default:
                Timber.w("Received unknown activity type: %s", activityType);
        }
    }

    private void receivedUnknown(PushNotification pushNotification) {
        Timber.e("Received unknown notification: %s", pushNotification.toString());
    }
}
