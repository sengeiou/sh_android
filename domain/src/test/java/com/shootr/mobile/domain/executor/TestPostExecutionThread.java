package com.shootr.mobile.domain.executor;

public class TestPostExecutionThread implements PostExecutionThread{

    @Override public void post(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void cancelPendingExecutions() {
        /* no-op */
    }
}
