package com.shootr.mobile.data.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.Sendable;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.ShotDispatcher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotDispatcherBackgroundService extends Service {

    private static final String EXTRA_SHOT = "shot";
    private static final String EXTRA_PRIVATE_MESSAGE = "privateMessage";
    private static final String EXTRA_IMAGE = "image";
    private static final String EXTRA_SEND_TYPE = "sendType";

    @Inject ShotDispatcher shotDispatcher;
    @Inject SessionRepository sessionRepository;
    @Inject @Local UserRepository localUserRepository;

    private int threadCounter;
    private final List<Thread> threadList = new ArrayList<>();

    public static void startService(Context context, Parcelable shot, File imageFile) {
        Intent serviceIntent = new Intent(context, ShotDispatcherBackgroundService.class);
        serviceIntent.putExtra(EXTRA_SHOT, shot);
        serviceIntent.putExtra(EXTRA_IMAGE, imageFile);
        if (shot instanceof ParcelableShot) {
            serviceIntent.putExtra(EXTRA_SEND_TYPE, EXTRA_SHOT);
        } else {
            serviceIntent.putExtra(EXTRA_SEND_TYPE, EXTRA_PRIVATE_MESSAGE);
        }
        context.startService(serviceIntent);
    }

    @Override public IBinder onBind(Intent intent) {
        throw new IllegalStateException("This service is not ment to bind");
    }

    @Override public void onCreate() {
        super.onCreate();
        ShootrApplication.get(getApplicationContext()).inject(this);
        restoreSessionOrLogin();
    }

    //region Login
    //TODO isolate login to avoid repetition (here and BaseSignedInActivity
    public boolean restoreSessionOrLogin() {
        if (isSessionActive()) {
            return true;
        } else {
            if (isSessionStored()) {
                restoreSession();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isSessionActive() {
        return sessionRepository.getCurrentUser() != null;
    }

    public boolean isSessionStored() {
        return sessionRepository.getSessionToken() != null && sessionRepository.getCurrentUserId() != null;
    }

    public void restoreSession() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        sessionRepository.setCurrentUser(currentUser);
    }
    //endregion

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        boolean serviceStartedExplicitly = intent != null;
        if (serviceStartedExplicitly) {
            handleSendType(intent);
        } else {
            serviceRestarted();
        }
        return START_STICKY;
    }

    private void handleSendType(Intent intent) {
        String sendType = intent.getStringExtra(EXTRA_SEND_TYPE);
        if (sendType.equals(EXTRA_SHOT)) {
            handleShot(intent);
        } else {
            handlePrivateMessage(intent);
        }
    }

    private void serviceRestarted() {
        String threadName = "shot_dispatcher_reset";
        RestartQueueRunnable runnable = new RestartQueueRunnable();
        Thread thread = new Thread(runnable, threadName);
        runnable.thread = thread;
        synchronized (threadList) {
            threadList.add(thread);
        }
        thread.start();
    }

    private void handleShot(Intent intent) {
        Shot shot = ((ParcelableShot) intent.getParcelableExtra(EXTRA_SHOT)).getShot();
        File imageFile = (File) intent.getSerializableExtra(EXTRA_IMAGE);
        sendBaseMessage(shot, imageFile);
    }

    private void handlePrivateMessage(Intent intent) {
        PrivateMessage privateMessage = ((ParcelablePrivateMessage)
            intent.getParcelableExtra(EXTRA_SHOT)).getPrivateMessage();
        File imageFile = (File) intent.getSerializableExtra(EXTRA_IMAGE);
        sendBaseMessage(privateMessage, imageFile);
    }

    private void sendBaseMessage(final Sendable shot, final File imageFile) {
        String threadName = "shot_dispatcher_" + threadCounter++;
        SendShotRunnable runnable = new SendShotRunnable(shot, imageFile);
        Thread thread = new Thread(runnable, threadName);
        runnable.thread = thread;
        synchronized (threadList) {
            threadList.add(thread);
        }
        thread.start();
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    private class SendShotRunnable implements Runnable {

        private final Sendable shot;
        private final File imageFile;
        Thread thread;

        public SendShotRunnable(Sendable shot, File imageFile) {
            this.shot = shot;
            this.imageFile = imageFile;
        }

        @Override public void run() {
            shotDispatcher.sendMessage(shot, imageFile);
            synchronized (threadList) {
                threadList.remove(thread);
            }
        }
    }

    private class RestartQueueRunnable implements Runnable {

        Thread thread;

        @Override public void run() {
            shotDispatcher.restartQueue();
            synchronized (threadList) {
                threadList.remove(thread);
            }
        }
    }
}
