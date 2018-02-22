package com.shootr.mobile.domain.executor;

import io.reactivex.Scheduler;

public interface RxPostExecutionThread {

  Scheduler getScheduler();
}
