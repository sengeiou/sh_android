package com.shootr.android.notifications.dagger;

import android.app.Application;
import android.support.v4.app.NotificationManagerCompat;
import com.shootr.android.domain.service.ShotQueueListener;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.shotqueue.ShotQueueNotificationListener;
import com.shootr.android.notifications.gcm.NotificationIntentReceiver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
