package com.shootr.android.service;

import com.shootr.android.domain.exception.ServerCommunicationException;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class RetrofitErrorHandler implements ErrorHandler {

    @Inject
    public RetrofitErrorHandler() {
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Timber.e(cause, "Retrofit error");

        Throwable originalError = cause.getCause();
        return originalError != null ? originalError : new ServerCommunicationException(cause);
    }
}
