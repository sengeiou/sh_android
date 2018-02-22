package com.shootr.mobile.interactor;

import com.shootr.mobile.domain.executor.ThreadExecutor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by miniserver on 25/1/18.
 */

public class JobExecutor implements ThreadExecutor {

  private ThreadPoolExecutor threadPoolExecutor;

  // Sets the amount of time an idle thread waits before terminating
  private static final int KEEP_ALIVE_TIME = 10;

  private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

  // Sets the Time Unit to seconds
  private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

  private final BlockingQueue<Runnable> workQueue;
  private final ThreadFactory threadFactory;

  @Inject JobExecutor() {
    this.threadFactory = new JobThreadFactory();
    this.workQueue = new LinkedBlockingQueue<>();
    setupNewExecutor();
  }

  protected void setupNewExecutor() {
    RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
      @Override public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
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


  @Override public void execute(Runnable runnable) {
    this.threadPoolExecutor.execute(runnable);
  }

  private static class JobThreadFactory implements ThreadFactory {
    private int counter = 0;

    @Override public Thread newThread(Runnable runnable) {
      return new Thread(runnable, "android_" + counter++);
    }
  }
}
