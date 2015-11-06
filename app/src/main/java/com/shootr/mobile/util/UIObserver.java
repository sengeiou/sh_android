package com.shootr.mobile.util;

import rx.Observer;

public abstract class UIObserver<T> implements Observer<T> {

    @Override
    public void onNext(T o) {
        /* no-op */
    }

    @Override public void onCompleted() {
        /* no-op */
    }

    @Override public void onError(Throwable e) {
        /* no-op */
    }
}
