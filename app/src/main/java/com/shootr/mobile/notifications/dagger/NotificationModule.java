package com.shootr.mobile.notifications.dagger;

import android.app.Application;
import android.support.v4.app.NotificationManagerCompat;

import com.shootr.mobile.domain.service.ShotQueueListener;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.shotqueue.ShotQueueNotificationListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    NotificationIntentReceiver.class,
  },
  complete = false,
  library = true
)
public class NotificationModule {


    @Provides @Singleton NotificationManagerCompat provideNotificationManagerCompat(Application application) {
        return NotificationManagerCompat.from(application);

    }

    @Provides @Singleton NotificationBuilderFactory provideNotificationBuilderFactory() {
        return new NotificationBuilderFactory();
    }

    @Provides @Singleton ShotQueueListener providesShotQueueListener(
      ShotQueueNotificationListener shotQueueNotificationListener) {
        return shotQueueNotificationListener;
    }

}
