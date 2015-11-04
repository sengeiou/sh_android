package com.shootr.mobile.domain.interactor;

public interface Interactor {

    interface Callback<Result> {

        void onLoaded(Result result);
    }

    interface ErrorCallback {

        void onError(com.shootr.mobile.domain.exception.ShootrException error);
    }

    interface CompletedCallback {

        void onCompleted();
    }

    void execute() throws Exception;
}
