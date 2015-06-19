package com.shootr.android.domain.interactor;

import com.shootr.android.domain.exception.ShootrException;

public interface Interactor {

    interface Callback<Result> {

        void onLoaded(Result result);
    }

    interface ErrorCallback {

        void onError(ShootrException error);
    }

    interface CompletedCallback {

        void onCompleted();
    }

    void execute() throws Exception;
}
