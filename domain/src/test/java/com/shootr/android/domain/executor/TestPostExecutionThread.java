package com.shootr.android.domain.executor;

public class TestPostExecutionThread implements PostExecutionThread{

    @Override public void post(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void cancelPendingExecutions() {
        /* no-op */
    }
}
