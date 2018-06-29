package com.shootr.mobile.data;

import com.shootr.mobile.data.bus.PrivacyLawEvent;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class PrivacyLawInterceptor implements Interceptor {

    public static final int CODE_PRIVACY_LAW = 451;

    private final BusPublisher busPublisher;

    @Inject PrivacyLawInterceptor(BusPublisher busPublisher) {
        this.busPublisher = busPublisher;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_PRIVACY_LAW) {
            busPublisher.post(new PrivacyLawEvent.Event());
        }
        return response;
    }
}
