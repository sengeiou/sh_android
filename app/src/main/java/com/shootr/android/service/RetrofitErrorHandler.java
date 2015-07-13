package com.shootr.android.service;

import android.support.annotation.NonNull;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.exception.ErrorResource;
import com.shootr.android.domain.exception.ServerCommunicationException;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class RetrofitErrorHandler implements ErrorHandler {

    @Inject
    public RetrofitErrorHandler() {
    }

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        ErrorResource errorResource = (ErrorResource) retrofitError.getBodyAs(ErrorResource.class);
        if (isApiError(errorResource)) {
            ApiException apiError = parseApiException(errorResource);
            Timber.e(apiError, "Retrofit API Error");
            return apiError;
        } else {
            Timber.e(retrofitError, "Retrofit Error");
            return originalCause(retrofitError);
        }
    }

    private boolean isApiError(ErrorResource errorResource) {
        return errorResource != null && errorResource.getCode() != null && errorResource.getStatus() != null;
    }

    @NonNull
    protected ApiException parseApiException(ErrorResource errorResource) {
        ErrorInfo errorInfo = ErrorInfo.getForHttpStatusAndCode(errorResource.getStatus(), errorResource.getCode());
        return new ApiException(errorInfo);
    }

    @NonNull
    protected Throwable originalCause(RetrofitError retrofitError) {
        Throwable originalError = retrofitError.getCause();
        return originalError != null ? originalError : defaultExceptionForUnknownCause(retrofitError);
    }

    @NonNull
    private Exception defaultExceptionForUnknownCause(RetrofitError retrofitError) {
        return new ServerCommunicationException(retrofitError);
    }
}
