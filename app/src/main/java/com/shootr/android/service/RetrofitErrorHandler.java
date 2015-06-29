package com.shootr.android.service;

import com.shootr.android.data.bus.ServerDown;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.exception.ServerCommunicationException;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class RetrofitErrorHandler implements ErrorHandler {

    private final BusPublisher busPublisher;

    @Inject
    public RetrofitErrorHandler(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Timber.e(cause, "Retrofit error");
        Throwable originalError = cause.getCause();

        //TODO Do the clean way
        int statusCode = cause.getResponse().getStatus();
        if (statusCode == 503) {
            ServerDownError error = (ServerDownError) cause.getBodyAs(ServerDownError.class);
            busPublisher.post(new ServerDown.Event(error.title, error.description));
            return new ServerCommunicationException(originalError);
        }

        return originalError != null ? originalError : cause;
    }

    private static class ServerDownError {

        public String title;
        public String description;
    }
}
