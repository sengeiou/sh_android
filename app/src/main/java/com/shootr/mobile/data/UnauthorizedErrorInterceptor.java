package com.shootr.mobile.data;

import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

public class UnauthorizedErrorInterceptor implements Interceptor {

    public static final int CODE_UNAUTHORIZED = 401;

    private final InteractorHandler interactorHandler;
    private final SafeDataClearRunnable safeDataClearRunnable;

    @Inject
    UnauthorizedErrorInterceptor(InteractorHandler interactorHandler, SafeDataClearRunnable safeDataClearRunnable) {
        this.interactorHandler = interactorHandler;
        this.safeDataClearRunnable = safeDataClearRunnable;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_UNAUTHORIZED) {
            interactorHandler.executeUnique(safeDataClearRunnable);
        }
        return response;
    }
}
