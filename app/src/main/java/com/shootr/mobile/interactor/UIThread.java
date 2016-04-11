/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.shootr.mobile.interactor;

import android.os.Handler;
import android.os.Looper;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MainThread (UI Thread) implementation based on a Handler instantiated with the main
 * application Looper.
 */
public class UIThread implements PostExecutionThread {

    private final Handler handler;
    private final Collection<CancellableRunnable> pendingRunnables;
    private final RunnableFinishedListener runnableFinishedListener = new RunnableFinishedListener() {
        @Override public void onFinished(CancellableRunnable cancellableRunnable) {
            pendingRunnables.remove(cancellableRunnable);
        }
    };

    public UIThread() {
        this.handler = new Handler(Looper.getMainLooper());
        pendingRunnables = new LinkedBlockingQueue<>();
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run on the main thread.
     *
     * @param runnable {@link Runnable} to be executed.
     */
    @Override public void post(Runnable runnable) {
        CancellableRunnable cancellableRunnable = new CancellableRunnable(runnable, runnableFinishedListener);
        pendingRunnables.add(cancellableRunnable);
        handler.post(cancellableRunnable);
    }

    @Override public void cancelPendingExecutions() {
        for (CancellableRunnable pendingRunnable : pendingRunnables) {
            pendingRunnable.cancel();
        }
    }

    private static class CancellableRunnable implements Runnable {

        private final Runnable wrappedRunnable;
        private final RunnableFinishedListener runnableFinishedListener;

        private boolean isCancelled;

        private CancellableRunnable(Runnable wrappedRunnable, RunnableFinishedListener runnableFinishedListener) {
            this.wrappedRunnable = wrappedRunnable;
            this.runnableFinishedListener = runnableFinishedListener;
        }

        public void cancel() {
            isCancelled = true;
        }

        @Override public void run() {
            if (!isCancelled) {
                wrappedRunnable.run();
                runnableFinishedListener.onFinished(this);
            }
        }
    }

    private interface RunnableFinishedListener {

        void onFinished(CancellableRunnable cancellableRunnable);
    }
}
