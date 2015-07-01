package com.shootr.android.data;

import com.shootr.android.data.bus.ServerDown;
import com.shootr.android.domain.bus.BusPublisher;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

class ServerDownErrorInterceptor implements Interceptor {

    public static final int CODE_SERVER_DOWN = 503;

    private final BusPublisher busPublisher;

    @Inject
    ServerDownErrorInterceptor(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_SERVER_DOWN) {
            busPublisher.post(new ServerDown.Event());
        }
        return response;
    }
}
