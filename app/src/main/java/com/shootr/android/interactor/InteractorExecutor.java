package com.shootr.android.interactor;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.squareup.otto.Bus;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

public class InteractorExecutor implements InteractorHandler {

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ThreadFactory threadFactory;

    private final Bus bus;

    @Inject public InteractorExecutor(@Main Bus bus) {
        this.bus = bus;
        this.workQueue = new LinkedBlockingQueue<>();
        this.threadFactory = new JobThreadFactory();
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Timber.w("Rejected execution of");
            }
        };
        this.threadPoolExecutor =
          new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES + 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
            this.workQueue, this.threadFactory, rejectedExecutionHandler);
    }

    @Override public void execute(final Interactor interactor) {
        if (interactor == null) {
            throw new IllegalArgumentException("Runnable to execute cannot be null");
        }
        Runnable interactorRunnable = new Runnable() {
            @Override public void run() {
                Timber.d("Running %s in thread %s", interactor.getClass().getSimpleName(),
                  Thread.currentThread().getName());
                try {
                    interactor.execute();
                } catch (Exception throwable) {
                    Timber.e(throwable, "IntractorExecutor error");
                }
            }
        };
        this.threadPoolExecutor.execute(interactorRunnable);
    }

    @Override public void sendUiMessage(Object objectToUi) {
        bus.post(objectToUi);
    }

    @Override public void sendError(Throwable throwable) {
        bus.post(throwable);
    }

    private static class JobThreadFactory implements ThreadFactory {

        private static final String THREAD_NAME = "android_";
        private int counter = 0;

        @Override public Thread newThread(Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter++);
        }
    }
}