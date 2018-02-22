package com.shootr.mobile.interactor;

import com.shootr.mobile.domain.executor.RxPostExecutionThread;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import javax.inject.Inject;

public class RxUiThread implements RxPostExecutionThread {
  @Inject RxUiThread() {
  }

  @Override public Scheduler getScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
