package com.shootr.mobile.domain.interactor;

import rx.Observer;

public abstract class OnCompletedObserver<T> implements Observer<T> {

    @Override public void onNext(T o) {
        /* no-op */
    }

    @Override public void onCompleted() {
        /* no-op */
    }
}
