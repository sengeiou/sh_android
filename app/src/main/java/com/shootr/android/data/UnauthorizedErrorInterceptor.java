package com.shootr.android.data;

import com.shootr.android.data.bus.Unauthorized;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

class UnauthorizedErrorInterceptor implements Interceptor {

    public static final int CODE_UNAUTHORIZED = 401;

    private final BusPublisher busPublisher;
    private final SessionRepository sessionRepository;
    private final DatabaseUtils databaseUtils;

    @Inject
    UnauthorizedErrorInterceptor(BusPublisher busPublisher,
      SessionRepository sessionRepository, DatabaseUtils databaseUtils) {
        this.busPublisher = busPublisher;
        this.sessionRepository = sessionRepository;
        this.databaseUtils = databaseUtils;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_UNAUTHORIZED) {
            databaseUtils.clearDataOnLogout();
            sessionRepository.destroySession();
            busPublisher.post(new Unauthorized.Event());
        }
        return response;
    }
}
