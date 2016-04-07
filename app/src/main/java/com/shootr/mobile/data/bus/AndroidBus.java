package com.shootr.mobile.data.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import timber.log.Timber;

public class AndroidBus extends Bus {

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public AndroidBus() {
        super(ThreadEnforcer.MAIN);
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
            Timber.e((Throwable) deadEvent.event, "Dead throwable in the bus");
        } else {
            Timber.d("Dead stream: %s", deadEvent.event);
        }
    }
}
