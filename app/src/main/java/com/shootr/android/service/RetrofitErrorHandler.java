package com.shootr.android.service;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class RetrofitErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Timber.e(cause, "Retrofit error");
        Throwable originalError = cause.getCause();
        return originalError != null ? originalError : cause;
    }
}
