package com.shootr.android.service;

import com.shootr.android.data.bus.ServerDown;
import com.shootr.android.domain.bus.BusPublisher;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class RetrofitErrorHandler implements ErrorHandler {

    public static final int CODE_SERVER_DOWN = 503;

    private final BusPublisher busPublisher;

    @Inject
    public RetrofitErrorHandler(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Timber.e(cause, "Retrofit error");

        processServerDownError(cause);

        Throwable originalError = cause.getCause();
        return originalError != null ? originalError : cause;
    }

    protected void processServerDownError(RetrofitError cause) {
        Response response = cause.getResponse();
        if (response != null && response.getStatus() == CODE_SERVER_DOWN) {
            ServerDownError error = (ServerDownError) cause.getBodyAs(ServerDownError.class);
            busPublisher.post(new ServerDown.Event(error.title, error.description));
        }
    }

    public static class ServerDownError {

        public String title;
        public String description;
    }
}
