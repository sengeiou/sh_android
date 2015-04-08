package com.shootr.android.domain.interactor;

import com.shootr.android.domain.exception.ShootrException;

public interface Interactor {

    interface ErrorCallback {

        void onError(ShootrException error);

    }
    void execute() throws Throwable;
}
