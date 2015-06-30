package com.shootr.android.service;

import com.shootr.android.data.bus.ServerDown;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.exception.ServerCommunicationException;
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
        return originalError != null ? originalError : new ServerCommunicationException(cause);
    }

    protected void processServerDownError(RetrofitError cause) {
        Response response = cause.getResponse();
        if (response != null && response.getStatus() == CODE_SERVER_DOWN) {
            busPublisher.post(new ServerDown.Event());
        }
    }
}
