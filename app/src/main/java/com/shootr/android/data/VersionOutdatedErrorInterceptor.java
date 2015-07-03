package com.shootr.android.data;

import com.shootr.android.data.bus.VersionOutdatedError;
import com.shootr.android.domain.bus.BusPublisher;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class VersionOutdatedErrorInterceptor implements Interceptor {

    public static final int CODE_PRECONDITION_FAILED = 412;
    private final BusPublisher busPublisher;

    @Inject public VersionOutdatedErrorInterceptor(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_PRECONDITION_FAILED) {
            busPublisher.post(new VersionOutdatedError.Event());
        }
        return response;
    }
}
