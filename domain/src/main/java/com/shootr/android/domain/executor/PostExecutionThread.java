package com.shootr.android.domain.executor;

public interface PostExecutionThread {

    void post(Runnable runnable);

    void cancelPendingExecutions();
}
