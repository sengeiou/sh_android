package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;

public interface Interactor {

    void execute() throws Exception;

    interface Callback<Result> {

        void onLoaded(Result result);
    }

    interface ErrorCallback {

        void onError(ShootrException error);
    }

    interface CompletedCallback {

        void onCompleted();
    }
}
