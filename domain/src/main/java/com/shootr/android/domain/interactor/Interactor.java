package com.shootr.android.domain.interactor;

public interface Interactor<T> {
    void execute() throws Throwable;
}
