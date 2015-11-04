package com.shootr.mobile.interactor;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.squareup.otto.Bus;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import timber.log.Timber;

public class InteractorExecutor implements InteractorHandler {

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor threadPoolExecutor;
    private final ThreadFactory threadFactory;
    private Thread uniqueThread;
    private AtomicBoolean uniqueThreadInProgress = new AtomicBoolean();

    private final Bus bus;
    private final PostExecutionThread postExecutionThread;

    @Inject
    public InteractorExecutor(@Main Bus bus, PostExecutionThread postExecutionThread) {
        this.bus = bus;
        this.postExecutionThread = postExecutionThread;
        this.workQueue = new LinkedBlockingQueue<>();
        this.threadFactory = new JobThreadFactory();
        this.setupNewExecutor();
    }

    protected void setupNewExecutor() {
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Timber.w("Rejected execution of %s", r.getClass().getSimpleName());
            }
        };
        this.threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES,
          NUMBER_OF_CORES + 2,
          KEEP_ALIVE_TIME,
          KEEP_ALIVE_TIME_UNIT,
          this.workQueue,
          this.threadFactory,
          rejectedExecutionHandler);
    }

    @Override
    public void execute(final Interactor interactor) {
        if (interactor == null) {
            throw new IllegalArgumentException("Runnable to execute cannot be null");
        }
        Runnable interactorRunnable = new Runnable() {
            @Override
            public void run() {
                Timber.d("-> Running %s in thread %s",
                  interactor.getClass().getSimpleName(),
                  Thread.currentThread().getName());
                try {
                    interactor.execute();
                } catch (Exception unhandledException) {
                    Timber.e(unhandledException,
                      "Unhandled exception while running %s. If this is an expected exception, it should be handled inside the Interactor.",
                      interactor.getClass().getSimpleName());
                    throw new RuntimeException(unhandledException);
                } finally {
                    Timber.d("<- Finished %s", interactor.getClass().getSimpleName());
                }
            }
        };
        this.threadPoolExecutor.execute(interactorRunnable);
    }

    @Override
    public void sendUiMessage(Object objectToUi) {
        bus.post(objectToUi);
    }

    @Override
    public void stopInteractors() {
        Timber.i("Stopping interactors...");
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Timber.e(e, "Oops. Interrupted while waiting for interactors to stop");
        }
        postExecutionThread.cancelPendingExecutions();
        setupNewExecutor();
    }

    @Override
    public void executeUnique(final Runnable runnable) {
        if (uniqueThreadInProgress.compareAndSet(false, true)) {
            uniqueThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Timber.i("-> Running unique thread...");
                    runnable.run();
                    uniqueThreadInProgress.set(false);
                    Timber.i("<- Unique thread done");
                }
            }, "unique_thread");
            uniqueThread.start();
        } else {
            Timber.i("Tried to run another unique thread, but ignored because there was one running already");
        }
    }

    private static class JobThreadFactory implements ThreadFactory {

        private static final String THREAD_NAME = "android_";
        private int counter = 0;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter++);
        }
    }
}