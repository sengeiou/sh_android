package com.shootr.android.data.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.shootr.android.ShootrApplication;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Queuer;
import java.io.File;
import javax.inject.Inject;

public class ShotDispatcherBackgroundService extends Service {

    private static final String EXTRA_SHOT = "shot";
    private static final String EXTRA_IMAGE = "image";

    @Inject @Queuer ShotSender shotSender;

    private int threadCounter;

    public static void startService(Context context, ParcelableShot shot, File imageFile) {
        Intent serviceIntent = new Intent(context, ShotDispatcherBackgroundService.class);
        serviceIntent.putExtra(EXTRA_SHOT, shot);
        serviceIntent.putExtra(EXTRA_IMAGE, imageFile);
        context.startService(serviceIntent);
    }

    @Override public IBinder onBind(Intent intent) {
        throw new IllegalStateException("This service is not ment to bind");
    }

    @Override public void onCreate() {
        super.onCreate();
        ShootrApplication.get(getApplicationContext()).inject(this);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleShot(intent);
        } else {
            serviceRestarted();
        }
        return START_STICKY;
    }

    private void serviceRestarted() {
    }

    private void handleShot(Intent intent) {
        Shot shot = ((ParcelableShot) intent.getParcelableExtra(EXTRA_SHOT)).getShot();
        File imageFile = (File) intent.getSerializableExtra(EXTRA_IMAGE);
        sendShot(shot, imageFile);
    }

    private void sendShot(final Shot shot, final File imageFile) {
        String threadName = "shot_dispatcher_" + threadCounter++;

        new Thread(new Runnable() {
            @Override public void run() {
                shotSender.sendShot(shot, imageFile);
            }
        }, threadName).start();
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
}
