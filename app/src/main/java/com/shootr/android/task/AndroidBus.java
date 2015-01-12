package com.shootr.android.task;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

public class AndroidBus extends Bus {

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public AndroidBus() {
        this.register(this);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

    @Subscribe
    public void deadEvent(DeadEvent deadEvent) {
        if (deadEvent.event instanceof Throwable) {
            Timber.w(((Throwable) deadEvent.event), "Dead throwable in te bus");
        } else {
            Timber.d("Dead event: %s", deadEvent.event);
        }
    }
}
