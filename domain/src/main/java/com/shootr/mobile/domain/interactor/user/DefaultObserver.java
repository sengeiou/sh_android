package com.shootr.mobile.domain.interactor.user;

import io.reactivex.observers.DisposableObserver;


public class DefaultObserver<T> extends DisposableObserver<T> {
  @Override public void onNext(T value) {
    /* no-op */
  }

  @Override public void onError(Throwable e) {
    /* no-op */
  }

  @Override public void onComplete() {
    /* no-op */
  }
}
