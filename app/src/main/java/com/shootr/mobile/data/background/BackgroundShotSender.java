package com.shootr.mobile.data.background;

import android.app.Application;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Sendable;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.service.MessageSender;
import java.io.File;
import javax.inject.Inject;

public class BackgroundShotSender implements MessageSender {

    private final Application application;

    @Inject public BackgroundShotSender(Application application) {
        this.application = application;
    }

    @Override public void sendMessage(Sendable sendable, File shotImage) {
        if (sendable instanceof Shot) {
            ShotDispatcherBackgroundService.startService(application,
                new ParcelableShot((Shot) sendable), shotImage);
        } else {
            ShotDispatcherBackgroundService.startService(application,
                new ParcelablePrivateMessage((PrivateMessage) sendable), shotImage);
        }
    }
}
