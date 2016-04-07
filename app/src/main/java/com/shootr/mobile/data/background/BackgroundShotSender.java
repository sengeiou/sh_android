package com.shootr.mobile.data.background;

import android.app.Application;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.service.ShotSender;

import java.io.File;

import javax.inject.Inject;

public class BackgroundShotSender implements ShotSender {

    private final Application application;

    @Inject public BackgroundShotSender(Application application) {
        this.application = application;
    }

    @Override public void sendShot(Shot shot, File shotImage) {
        ShotDispatcherBackgroundService.startService(application, new ParcelableShot(shot), shotImage);
    }
}
