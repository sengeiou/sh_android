package com.shootr.mobile.ui;

import android.os.Handler;
import com.shootr.mobile.data.prefs.BooleanPreference;
import timber.log.Timber;

public class DebugPoller extends Poller {

    private final BooleanPreference pollerEnabled;

    public DebugPoller(Handler handler, BooleanPreference pollerEnabled) {
        super(handler);
        this.pollerEnabled = pollerEnabled;
    }

    @Override
    public void scheduleNextPoll() {
        if (pollerEnabled.get()) {
            super.scheduleNextPoll();
            Timber.d("Sheduled next poll in " + this.getIntervalMilliseconds());
        }
    }

    @Override
    public void startPolling() {
        if (pollerEnabled.get()) {
            Timber.d("Staring polling");
            super.startPolling();
        }
    }

    @Override
    public void stopPolling() {
        Timber.d("Stopping polling");
        super.stopPolling();
    }

    @Override
    protected void pollNow() {
        if (pollerEnabled.get()) {
            Timber.d("Polling now!");
            super.pollNow();
        }
    }
}
