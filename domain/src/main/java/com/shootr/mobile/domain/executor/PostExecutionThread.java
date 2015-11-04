package com.shootr.mobile.domain.executor;

public interface PostExecutionThread {

    void post(Runnable runnable);

    void cancelPendingExecutions();
}
