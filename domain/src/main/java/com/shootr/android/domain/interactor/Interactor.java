package com.shootr.android.domain.interactor;

public interface Interactor<T> {
    T execute() throws Throwable;
}
