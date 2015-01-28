package com.shootr.android.domain.executor;

public class TestPostExecutionThread implements PostExecutionThread{

    @Override public void post(Runnable runnable) {
        runnable.run();
    }
}
